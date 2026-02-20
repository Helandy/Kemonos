package su.afk.kemonos.creatorPost.presenter

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.creatorPost.domain.model.video.VideoThumbState
import su.afk.kemonos.creatorPost.domain.useCase.GetCommentsUseCase
import su.afk.kemonos.creatorPost.domain.useCase.GetMediaMetaUseCase
import su.afk.kemonos.creatorPost.domain.useCase.GetPostUseCase
import su.afk.kemonos.creatorPost.navigation.CreatorPostDest
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.*
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.Effect.OpenAudio
import su.afk.kemonos.creatorPost.presenter.delegates.LikeDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.MediaMetaDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.NavigateDelegates
import su.afk.kemonos.creatorPost.presenter.helper.collectDownloadAllItems
import su.afk.kemonos.creatorProfile.api.IGetProfileUseCase
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.error.error.toFavoriteToastBar
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.TranslateTarget
import su.afk.kemonos.ui.presenter.androidView.cleanDuplicatedMediaFromContent
import su.afk.kemonos.ui.presenter.androidView.clearHtml
import su.afk.kemonos.ui.presenter.androidView.htmlToBlocks
import su.afk.kemonos.ui.presenter.androidView.model.PostBlock
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.shared.ShareLinkBuilder
import su.afk.kemonos.ui.shared.model.ShareTarget
import su.afk.kemonos.ui.translate.TextTranslator
import su.afk.kemonos.ui.translate.preprocessForTranslation
import su.afk.kemonos.ui.uiUtils.format.audioMimeType
import su.afk.kemonos.ui.uiUtils.format.buildFileUrl
import java.net.URLEncoder

internal class CreatorPostViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorPostDest.CreatorPost,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val getProfileUseCase: IGetProfileUseCase,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val domainResolver: IDomainResolver,
    private val getMediaMetaUseCase: GetMediaMetaUseCase,
    private val likeDelegate: LikeDelegate,
    private val navigateDelegates: NavigateDelegates,
    private val downloadUtil: IDownloadUtil,
    private val translator: TextTranslator,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {
    private var loadingJob: Job? = null
    private var loadingRequestId: Long = 0L

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorPostDest.CreatorPost): CreatorPostViewModel
    }

    override fun createInitialState(): State = State.default()

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
                loading = true,
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
            Event.Back -> navigateDelegates.navigateBack()

            Event.CopyPostLinkClicked -> copyPostLink()
            Event.FavoriteClicked -> onFavoriteClick()

            Event.CreatorHeaderClicked -> navigateToCreatorProfile()

            is Event.ToggleTranslate -> onToggleTranslate()

            is Event.OpenImage -> navigateOpenImage(event.originalUrl)

            is Event.Download -> download(event.url, event.fileName)
            Event.DownloadAllClicked -> downloadAll()

            is Event.VideoThumbRequested -> requestVideoMeta(event.server, event.path)
            is Event.VideoInfoRequested -> requestVideoMeta(event.server, event.path)
            is Event.AudioInfoRequested -> requestAudioMeta(event.url)

            is Event.PlayAudio -> {
                val safeName = event.name?.takeIf { it.isNotBlank() } ?: event.url.substringAfterLast('/')
                val mime = audioMimeType(event.url)
                setEffect(OpenAudio(event.url, safeName, mime))
            }

            Event.OpenNextPost -> {
                val next = currentState.post?.post?.nextId ?: return

                resetForNewPost(next)
                loadingPost()
            }

            Event.OpenPrevPost -> {
                val prev = currentState.post?.post?.prevId ?: return

                resetForNewPost(prev)
                loadingPost()
            }
        }
    }

    fun loadingPost() {
        loadingJob?.cancel()
        val requestId = ++loadingRequestId
        val requestService = currentState.service
        val requestCreatorId = currentState.id
        val requestPostId = currentState.postId

        loadingJob = viewModelScope.launch {
            setState { copy(loading = true) }

            val postDeferred = async { getPostUseCase(requestService, requestCreatorId, requestPostId) }
            val commentsDeferred = async { getCommentsUseCase(requestService, requestCreatorId, requestPostId) }

            /** шапка профиля не всегда нужна */
            val profileDeferred = async { getProfileUseCase(service = requestService, id = requestCreatorId) }

            val post = postDeferred.await()
            val comments = commentsDeferred.await()
            val profile = profileDeferred.await()

            val showButtonTranslate = post?.post?.content?.clearHtml()?.isNotBlank() ?: false
            val mediaRefs = post?.collectMediaRefsForDedup()
            val siteBaseUrl = getCurrentSiteRootUrlUseCase()

            val cleanContent = withContext(Dispatchers.Default) {
                cleanDuplicatedMediaFromContent(
                    html = post?.post?.content.orEmpty().take(MAX_HTML_CHARS),
                    attachmentPaths = mediaRefs.orEmpty(),
                )
            }

            val blocks = withContext(Dispatchers.Default) {
                htmlToBlocks(cleanContent, siteBaseUrl)
            }

            if (requestId != loadingRequestId) return@launch

            setState {
                copy(
                    loading = false,
                    post = post,
                    showButtonTranslate = showButtonTranslate,
                    contentBlocks = blocks,

                    commentDomains = comments,
                    profile = profile,

                    translateExpanded = false,
                    translateLoading = false,
                    translateText = null,
                    translateError = null,
                )
            }

            val isShowAvailable = likeDelegate.postIsAvailableLike()
            if (requestId != loadingRequestId) return@launch

            if (isShowAvailable) {
                val favorite = likeDelegate.isPostFavorite(
                    service = requestService,
                    creatorId = requestCreatorId,
                    postId = requestPostId,
                )
                if (requestId != loadingRequestId) return@launch
                setState { copy(isFavorite = favorite) }
            }

            if (requestId != loadingRequestId) return@launch
            setState { copy(isFavoriteShowButton = isShowAvailable) }
        }
    }

    private val mediaMetaDelegate = MediaMetaDelegate(
        scope = viewModelScope,
        getMediaMeta = getMediaMetaUseCase,
        timeoutMs = 15_000L
    )

    private fun requestVideoMeta(server: String, path: String) {
        val url = buildFileUrl(server, path)
        val needInfo = currentState.videoInfo[url] !is MediaInfoState.Success
        val needThumb = currentState.videoThumbs[url] !is VideoThumbState.Success
        if (!needInfo && !needThumb) return

        // loading
        setState {
            copy(
                videoInfo = if (needInfo) videoInfo + (url to MediaInfoState.Loading) else videoInfo,
                videoThumbs = if (needThumb) videoThumbs + (url to VideoThumbState.Loading) else videoThumbs,
            )
        }

        mediaMetaDelegate.requestVideo(
            url = url,
            path = path,
            onSuccess = { meta ->
                setState {
                    copy(
                        videoInfo = videoInfo + (url to MediaInfoState.Success(meta.info)),
                        videoThumbs = videoThumbs + (url to (meta.frame?.let { VideoThumbState.Success(it) }
                            ?: VideoThumbState.Error(null)))
                    )
                }
            },
            onError = { t ->
                setState {
                    copy(
                        videoInfo = if (needInfo) videoInfo + (url to MediaInfoState.Error(t)) else videoInfo,
                        videoThumbs = if (needThumb) videoThumbs + (url to VideoThumbState.Error(t)) else videoThumbs,
                    )
                }
            }
        )
    }

    private fun requestAudioMeta(url: String) {
        val needInfo = currentState.audioInfo[url] !is MediaInfoState.Success
        if (!needInfo) return

        setState { copy(audioInfo = audioInfo + (url to MediaInfoState.Loading)) }

        mediaMetaDelegate.requestAudio(
            url = url,
            onSuccess = { meta ->
                setState { copy(audioInfo = audioInfo + (url to MediaInfoState.Success(meta.info))) }
            },
            onError = { t ->
                setState { copy(audioInfo = audioInfo + (url to MediaInfoState.Error(t))) }
            }
        )
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

    /** навиагция на профиль автора */
    fun navigateToCreatorProfile() {
        viewModelScope.launch {
            navigateDelegates.navigateToCreatorProfile(currentState.id, currentState.service)
        }
    }

    fun navigateOpenImage(originalUrl: String) {
        val imageUrls = collectImageGalleryUrls(selectedUrl = originalUrl)
        val selectedIndex = imageUrls.indexOf(originalUrl).takeIf { it >= 0 }

        navigateDelegates.navigateOpenImage(
            originalUrl = originalUrl,
            imageUrls = imageUrls,
            selectedIndex = selectedIndex,
        )
    }

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
        viewModelScope.launch {
            enqueueDownload(url = url, fileName = fileName)
        }
    }

    private fun downloadAll() {
        val post = currentState.post ?: return
        val fallbackBaseUrl = domainResolver.baseUrlByService(currentState.service)
        val allItems = post.collectDownloadAllItems(fallbackBaseUrl = fallbackBaseUrl)
        if (allItems.isEmpty()) return

        viewModelScope.launch {
            allItems.forEach { item ->
                enqueueDownload(url = item.url, fileName = item.fileName)
            }
        }
    }

    private suspend fun enqueueDownload(url: String, fileName: String?) {
        downloadUtil.enqueueSystemDownload(
            url = url,
            fileName = fileName,
            service = currentState.service,
            creatorName = currentState.profile?.name,
            postId = currentState.postId,
            postTitle = currentState.post?.post?.title
        )
        setEffect(Effect.DownloadToast(fileName.orEmpty()))
    }

    fun onToggleTranslate() {
        val plainText = currentState.post?.post?.content?.preprocessForTranslation()
        if (plainText.isNullOrEmpty()) return

        val nextExpanded = !currentState.translateExpanded
        setState { copy(translateExpanded = nextExpanded) }

        if (!nextExpanded) return

        when (currentState.uiSettingModel.translateTarget) {
            TranslateTarget.GOOGLE -> {
                setState { copy(translateExpanded = false) }

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
                    text = plainText,
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

    private fun collectImageGalleryUrls(selectedUrl: String): List<String> {
        val contentImages = currentState.contentBlocks
            .orEmpty()
            .mapNotNull { block -> (block as? PostBlock.Image)?.url }
            .filter { it.isNotBlank() }

        val previewImages = currentState.post
            ?.previews
            .orEmpty()
            .asSequence()
            .filter { it.type == "thumbnail" }
            .mapNotNull(::buildPreviewFullUrl)
            .toList()

        val merged = (contentImages + previewImages).distinct()
        return if (selectedUrl in merged) merged else (listOf(selectedUrl) + merged).distinct()
    }

    private fun buildPreviewFullUrl(preview: PreviewDomain): String? {
        val server = preview.server ?: return null
        val path = preview.path ?: return null
        val name = preview.name ?: return null

        val encodedName = URLEncoder.encode(name, "UTF-8")
        return "$server/data$path?f=$encodedName"
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

    companion object {
        const val MAX_HTML_CHARS = 100_000
    }

    private fun resetForNewPost(nextPostId: String) = setState {
        copy(
            postId = nextPostId,
            loading = true,

            post = null,
            showButtonTranslate = false,
            contentBlocks = null,
            commentDomains = emptyList(),

            translateExpanded = false,
            translateLoading = false,
            translateText = null,
            translateError = null,

            videoThumbs = emptyMap(),
            videoInfo = emptyMap(),
            audioInfo = emptyMap(),
        )
    }

}
