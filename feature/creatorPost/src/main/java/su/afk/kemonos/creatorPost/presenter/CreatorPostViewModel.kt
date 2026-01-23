package su.afk.kemonos.creatorPost.presenter

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.error.toFavoriteToastBar
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.common.presenter.webView.util.cleanDuplicatedMediaFromContent
import su.afk.kemonos.common.shared.ShareLinkBuilder
import su.afk.kemonos.common.shared.ShareTarget
import su.afk.kemonos.common.translate.TextTranslator
import su.afk.kemonos.common.translate.preprocessForTranslation
import su.afk.kemonos.common.util.audioMimeType
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.creatorPost.domain.useCase.GetCommentsUseCase
import su.afk.kemonos.creatorPost.domain.useCase.GetMediaInfoUseCase
import su.afk.kemonos.creatorPost.domain.useCase.GetPostUseCase
import su.afk.kemonos.creatorPost.navigation.CreatorPostDest
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.*
import su.afk.kemonos.creatorPost.presenter.delegates.LikeDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.NavigateDelegates
import su.afk.kemonos.creatorProfile.api.IGetProfileUseCase
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.TranslateTarget

internal class CreatorPostViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorPostDest.CreatorPost,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val getProfileUseCase: IGetProfileUseCase,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val getVideoInfo: GetMediaInfoUseCase,
    private val likeDelegate: LikeDelegate,
    private val navigateDelegates: NavigateDelegates,
    private val downloadUtil: IDownloadUtil,
    private val translator: TextTranslator,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorPostDest.CreatorPost): CreatorPostViewModel
    }

    override fun createInitialState(): State = State()

    override fun onRetry() {
        loadingPost()
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    init {
        observeUiSetting()
        setState {
            copy(
                service = dest.service,
                id = dest.id,
                postId = dest.postId,
                showBarCreator = dest.showBarCreator
            )
        }

        loadingPost()
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Retry -> loadingPost()

            Event.CopyPostLinkClicked -> copyPostLink()
            Event.FavoriteClicked -> onFavoriteClick()

            Event.CreatorHeaderClicked -> navigateToCreatorProfile()

            is Event.ToggleTranslate -> onToggleTranslate(event.rawHtml)

            is Event.OpenImage -> navigateOpenImage(event.originalUrl)

            is Event.Download -> download(event.url, event.fileName)

            is Event.OpenExternalUrl -> setEffect(Effect.OpenUrl(event.url))

            is Event.VideoInfoRequested -> requestVideoInfo(event.url)
            is Event.AudioInfoRequested -> requestAudioInfo(event.url)

            is Event.PlayAudio -> {
                val safeName = event.name?.takeIf { it.isNotBlank() } ?: event.url.substringAfterLast('/')
                val mime = audioMimeType(event.url)
                setEffect(Effect.OpenAudio(event.url, safeName, mime))
            }
        }
    }

    fun loadingPost() = viewModelScope.launch {
        setState { copy(loading = true) }

        val postDeferred = async { getPostUseCase(currentState.service, currentState.id, currentState.postId) }
        val commentsDeferred = async { getCommentsUseCase(currentState.service, currentState.id, currentState.postId) }

        /** шапка профиля не всегда нужна */
        val profileDeferred = if (currentState.showBarCreator) {
            async {
                getProfileUseCase(service = currentState.service, id = currentState.id)
            }
        } else {
            null
        }

        val post = postDeferred.await()
        val comments = commentsDeferred.await()
        val profile = profileDeferred?.await()

        val mediaRefs = post?.collectMediaRefsForDedup()
        val cleanContent = cleanDuplicatedMediaFromContent(
            html = post?.post?.content.orEmpty(),
            attachmentPaths = mediaRefs.orEmpty(),
        )

        setState {
            copy(
                loading = false,
                post = post,
                postContentClean = cleanContent,

                commentDomains = comments,
                profile = profile,

                translateExpanded = false,
                translateLoading = false,
                translateText = null,
                translateError = null,
            )
        }

        isPostFavorite()
    }

    /** Получить/создать flow для конкретного видео и стартануть загрузку при необходимости */
    private val videoJobs = mutableMapOf<String, Job>()
    private fun requestVideoInfo(url: String) {
        // уже есть успешное/ошибка — решай сам: можно не перезапрашивать
        val existing = currentState.videoInfo[url]
        if (existing is MediaInfoState.Success) return

        // если уже грузим — не стартуем второй раз
        if (videoJobs[url]?.isActive == true) return

        // если хотим показать loading сразу
        setState {
            copy(videoInfo = videoInfo + (url to MediaInfoState.Loading))
        }

        videoJobs[url] = viewModelScope.launch {
            runCatching { getVideoInfo(url) }
                .onSuccess { info ->
                    setState { copy(videoInfo = videoInfo + (url to MediaInfoState.Success(info))) }
                }
                .onFailure { e ->
                    setState { copy(videoInfo = videoInfo + (url to MediaInfoState.Error(e))) }
                }
        }
    }

    private val audioJobs = mutableMapOf<String, Job>()
    private fun requestAudioInfo(url: String) {
        val existing = currentState.audioInfo[url]
        if (existing is MediaInfoState.Success) return
        if (audioJobs[url]?.isActive == true) return

        setState { copy(audioInfo = audioInfo + (url to MediaInfoState.Loading)) }

        audioJobs[url] = viewModelScope.launch {
            runCatching { getVideoInfo(url) }
                .onSuccess { info ->
                    setState { copy(audioInfo = audioInfo + (url to MediaInfoState.Success(info))) }
                }
                .onFailure { e ->
                    setState { copy(audioInfo = audioInfo + (url to MediaInfoState.Error(e))) }
                }
        }
    }

    /** Избранное */
    fun onFavoriteClick() = viewModelScope.launch {
        if (currentState.favoriteActionLoading) return@launch

        val wasFavorite = currentState.isFavorite
        setState { copy(favoriteActionLoading = true) }

        val result = likeDelegate.onFavoriteClick(
            isFavorite = wasFavorite,
            post = currentState.post,
            service = currentState.service,
            creatorId = currentState.id,
            postId = currentState.postId
        )

        result
            .onSuccess {
                setState { copy(isFavorite = !wasFavorite) }
            }
            .onFailure { t ->
                val errorMessage = errorHandler.parse(t).toFavoriteToastBar()
                setEffect(Effect.ShowToast(errorMessage))
            }
        setState { copy(favoriteActionLoading = false) }
    }

    /** Проверит в избранном ли пост */
    fun isPostFavorite() = viewModelScope.launch {
        val isShowAvailable = likeDelegate.postIsAvailableLike()
        if (isShowAvailable) {
            val favorite = likeDelegate.isPostFavorite(
                service = currentState.service,
                creatorId = currentState.id,
                postId = currentState.postId,
            )
            setState { copy(isFavorite = favorite) }
        }
        setState { copy(isFavoriteShowButton = isShowAvailable) }
    }

    /** навиагция на профиль автора */
    fun navigateToCreatorProfile() {
        viewModelScope.launch {
            navigateDelegates.navigateToCreatorProfile(currentState.id, currentState.service)
        }
    }

    fun navigateOpenImage(originalUrl: String) = navigateDelegates.navigateOpenImage(originalUrl)

    /** Копирование в буфер */
    fun copyPostLink() {
        val url = ShareLinkBuilder.build(
            ShareTarget.Post(
                siteRoot = getCurrentSiteRootUrlUseCase(),
                service = currentState.service,
                userId = currentState.id,
                postId = currentState.postId
            )
        )
        setEffect(Effect.CopyPostLink(url))
    }

    fun download(url: String, fileName: String?) {
        downloadUtil.enqueueSystemDownload(
            url = url,
            fileName = fileName,
            mimeType = null
        )
        setEffect(
            Effect.DownloadToast(fileName.orEmpty())
        )
    }

    fun onToggleTranslate(rawHtml: String) {
        val nextExpanded = !currentState.translateExpanded
        setState { copy(translateExpanded = nextExpanded) }

        if (!nextExpanded) return

        when (currentState.uiSettingModel.translateTarget) {
            TranslateTarget.GOOGLE -> {
                setState { copy(translateExpanded = false) }

                val plainText = rawHtml.preprocessForTranslation()
                if (plainText.isBlank()) return

                setEffect(
                    Effect.OpenGoogleTranslate(
                        text = plainText,
                        targetLangTag = currentState.uiSettingModel.translateLanguageTag
                    )
                )
                return
            }

            TranslateTarget.APP -> Unit
        }

        if (currentState.translateText != null && currentState.translateError == null) return
        if (currentState.translateLoading) return

        viewModelScope.launch {
            setState { copy(translateLoading = true, translateError = null) }

            runCatching {
                translator.translateAuto(
                    text = rawHtml,
                    targetLangTag = currentState.uiSettingModel.translateLanguageTag
                )
            }.onSuccess { text ->
                setState { copy(translateText = text, translateLoading = false) }
            }.onFailure { e ->
                setState {
                    copy(
                        translateLoading = false,
                        translateError = e.message ?: "Translation error"
                    )
                }
            }
        }
    }

    fun PostContentDomain.collectMediaRefsForDedup(): List<String> = buildList {
        // attachments в корне ответа
        addAll(attachments.map { it.path })
        // previews (чаще всего thumbnail/path)
        addAll(previews.mapNotNull { it.path })
        // если у PreviewDomain есть url
        addAll(previews.mapNotNull { it.url })

        // вложенные attachments/file внутри post
        post.file?.path?.let(::add)
        addAll(post.attachments.map { it.path })
    }.filter { it.isNotBlank() }
}