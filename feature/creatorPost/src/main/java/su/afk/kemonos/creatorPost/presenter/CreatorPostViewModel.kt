package su.afk.kemonos.creatorPost.presenter

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorPost.domain.media.model.CommonMediaInfo
import su.afk.kemonos.creatorPost.domain.media.model.MediaInfoState
import su.afk.kemonos.creatorPost.navigation.CreatorPostDestination
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.*
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.Effect.OpenAudio
import su.afk.kemonos.creatorPost.presenter.delegates.LikeDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.MediaMetaDelegateNew
import su.afk.kemonos.creatorPost.presenter.delegates.NavigateDelegates
import su.afk.kemonos.creatorPost.presenter.delegates.PostLoadDelegate
import su.afk.kemonos.creatorPost.presenter.helper.collectDownloadAllItems
import su.afk.kemonos.creatorPost.presenter.model.LoadRequest
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.error.error.toFavoriteToastBar
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.TranslateTarget
import su.afk.kemonos.ui.presenter.androidView.model.PostBlock
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.shared.ShareLinkBuilder
import su.afk.kemonos.ui.shared.model.ShareTarget
import su.afk.kemonos.ui.translate.TextTranslator
import su.afk.kemonos.ui.translate.preprocessForTranslation
import java.net.URLEncoder

internal class CreatorPostViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorPostDestination.CreatorPost,
    private val postLoadDelegate: PostLoadDelegate,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val domainResolver: IDomainResolver,
    private val mediaMetaDelegateNew: MediaMetaDelegateNew,
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
        fun create(dest: CreatorPostDestination.CreatorPost): CreatorPostViewModel
    }

    /** Начальное состояние экрана поста */
    override fun createInitialState(): State = State.default()

    /** Повторная загрузка после ошибки */
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

    /** Обработка пользовательских событий экрана */
    override fun onEvent(event: Event) {
        when (event) {
            Event.Retry -> loadingPost()
            Event.Back -> navigateDelegates.navigateBack()

            Event.CopyPostLinkClicked -> copyPostLink()
            Event.FavoriteClicked -> onFavoriteClick()

            Event.CreatorHeaderClicked -> navigateToCreatorProfile()
            Event.ShowCreatorBanner -> setState { copy(showBarCreator = true) }
            is Event.TagClicked -> navigateToCreatorProfileByTag(event.tag)

            is Event.ToggleTranslate -> onToggleTranslate()

            is Event.OpenImage -> navigateOpenImage(event.originalUrl)

            is Event.Download -> download(event.url, event.fileName)
            Event.DownloadAllClicked -> downloadAll()

            is Event.VideoInfoRequested -> requestVideoMeta(event.server, event.path)
            is Event.AudioInfoRequested -> requestAudioMeta(event.server, event.path)

            is Event.PlayAudio -> {
                val safeName = event.name?.takeIf { it.isNotBlank() } ?: event.url.substringAfterLast('/')
                setEffect(OpenAudio(event.url, safeName, event.mime))
            }

            is Event.SelectRevision -> onSelectRevision(event.revisionId)
            Event.ShareStarted -> onShareStarted()
            is Event.ShareProgress -> onShareProgress(event.bytesRead, event.totalBytes)
            Event.ShareFinished -> onShareFinished()

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

    /** Полная загрузка поста/комментариев/профиля и применение в state */
    fun loadingPost() {
        loadingJob?.cancel()
        val request = createLoadRequest()

        loadingJob = viewModelScope.launch {
            setState { copy(loading = true) }
            val loaded = postLoadDelegate.load(request)

            if (!isLatestRequest(request)) return@launch

            setState {
                copy(
                    loading = false,
                    sourcePost = loaded.sourcePost,
                    post = loaded.resolvedPost,
                    revisionIds = loaded.revisionIds,
                    selectedRevisionId = loaded.selectedRevisionId,
                    showButtonTranslate = loaded.showButtonTranslate,
                    contentBlocks = loaded.contentBlocks,
                    commentDomains = loaded.comments,
                    profile = loaded.profile,

                    translateExpanded = false,
                    translateLoading = false,
                    translateText = null,
                    translateError = null,
                )
            }

            applyFavoriteState(request)
        }
    }

    /** Переключение версии поста (revision) без полной перезагрузки */
    private fun onSelectRevision(revisionId: Int?) {
        val sourcePost = currentState.sourcePost ?: return
        if (currentState.selectedRevisionId == revisionId) return

        viewModelScope.launch {
            val loadedRevision = postLoadDelegate.loadRevision(sourcePost, revisionId)

            setState {
                copy(
                    post = loadedRevision.resolvedPost,
                    selectedRevisionId = revisionId,
                    showButtonTranslate = loadedRevision.showButtonTranslate,
                    contentBlocks = loadedRevision.contentBlocks,
                    translateExpanded = false,
                    translateLoading = false,
                    translateText = null,
                    translateError = null,
                )
            }
        }
    }

    /** Снимок параметров текущего запроса загрузки */
    private fun createLoadRequest(): LoadRequest {
        return LoadRequest(
            requestId = ++loadingRequestId,
            service = currentState.service,
            creatorId = currentState.id,
            postId = currentState.postId,
            showComments = currentState.uiSettingModel.showCommentsInPost,
        )
    }

    /** Проверка, что ответ относится к последнему активному запросу */
    private fun isLatestRequest(request: LoadRequest): Boolean {
        return request.requestId == loadingRequestId
    }

    /** Обновляет состояние избранного и видимость кнопки лайка */
    private suspend fun applyFavoriteState(request: LoadRequest) {
        val isShowAvailable = likeDelegate.postIsAvailableLike()
        if (!isLatestRequest(request)) return

        val favorite = if (isShowAvailable) {
            likeDelegate.isPostFavorite(
                service = request.service,
                creatorId = request.creatorId,
                postId = request.postId,
            )
        } else {
            null
        }
        if (!isLatestRequest(request)) return

        setState {
            copy(
                isFavoriteShowButton = isShowAvailable,
                isFavorite = favorite ?: isFavorite
            )
        }
    }

    /** Запрашивает мета-информацию видео */
    private fun requestVideoMeta(server: String, path: String) = viewModelScope.launch {
        val currentInfoState = currentState.videoInfo[path]
        if (currentInfoState is MediaInfoState.Success || currentInfoState is MediaInfoState.Loading) return@launch
        val useExternalMetaData = currentState.uiSettingModel.useExternalMetaData
        val service = currentState.service

        setState {
            copy(
                videoInfo = videoInfo + (path to MediaInfoState.Loading),
            )
        }

        runCatching {
            getVideoMetaWithFallback(
                useExternalMetaData = useExternalMetaData,
                service = service,
                server = server,
                path = path,
            )
        }.onSuccess { result ->
            setState {
                copy(
                    videoInfo = videoInfo + (path to MediaInfoState.Success(result)),
                )
            }
        }.onFailure { error ->
            setState {
                copy(
                    videoInfo = videoInfo + (path to MediaInfoState.Error(error)),
                )
            }
        }
    }

    /** Запрашивает мета-информацию аудио */
    private fun requestAudioMeta(server: String?, path: String) = viewModelScope.launch {
        val currentInfoState = currentState.audioInfo[path]
        if (currentInfoState is MediaInfoState.Success || currentInfoState is MediaInfoState.Loading) return@launch
        val useExternalMetaData = currentState.uiSettingModel.useExternalMetaData
        val service = currentState.service

        setState {
            copy(
                audioInfo = audioInfo + (path to MediaInfoState.Loading),
            )
        }

        runCatching {
            getAudioMetaWithFallback(
                useExternalMetaData = useExternalMetaData,
                service = service,
                server = server,
                path = path,
            )
        }.onSuccess { result ->
            setState {
                copy(
                    audioInfo = audioInfo + (path to MediaInfoState.Success(result)),
                )
            }
        }.onFailure { error ->
            setState {
                copy(
                    audioInfo = audioInfo + (path to MediaInfoState.Error(error)),
                )
            }
        }
    }

    private suspend fun getVideoMetaWithFallback(
        useExternalMetaData: Boolean,
        service: String,
        server: String?,
        path: String,
    ): CommonMediaInfo {
        return runCatching {
            mediaMetaDelegateNew.getVideoInfo(
                isRemote = useExternalMetaData,
                server = server,
                service = service,
                path = path,
            )
        }.recoverCatching { error ->
            if (!useExternalMetaData) throw error
            mediaMetaDelegateNew.getVideoInfo(
                isRemote = false,
                server = server,
                service = service,
                path = path,
            )
        }.getOrThrow()
    }

    private suspend fun getAudioMetaWithFallback(
        useExternalMetaData: Boolean,
        service: String,
        server: String?,
        path: String,
    ): CommonMediaInfo {
        return runCatching {
            mediaMetaDelegateNew.getAudioInfo(
                isRemote = useExternalMetaData,
                service = service,
                server = server,
                path = path,
            )
        }.recoverCatching { error ->
            if (!useExternalMetaData) throw error
            mediaMetaDelegateNew.getAudioInfo(
                isRemote = false,
                service = service,
                server = server,
                path = path,
            )
        }.getOrThrow()
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

    /** Навигация на профиль автора с учетом выбранного тега */
    private fun navigateToCreatorProfileByTag(tag: String) {
        if (tag.isBlank()) return

        viewModelScope.launch {
            navigateDelegates.navigateToCreatorProfileByTag(
                id = currentState.id,
                service = currentState.service,
                tag = tag
            )
        }
    }

    /** Открывает экран изображения и собирает галерею для свайпа */
    fun navigateOpenImage(originalUrl: String) {
        val imageUrlsWithThumbnails = collectImageGalleryUrlsWithThumbnails(selectedUrl = originalUrl)
        val thumbnailUrlsMap = mutableMapOf<String, String>()
        val imageUrls = imageUrlsWithThumbnails.map { (fullUrl, thumbnailUrl) ->
            thumbnailUrl?.let { thumbnailUrlsMap[fullUrl] = it }
            fullUrl
        }
        val selectedIndex = imageUrls.indexOf(originalUrl).takeIf { it >= 0 }

        navigateDelegates.navigateOpenImage(
            originalUrl = originalUrl,
            imageUrls = imageUrls,
            selectedIndex = selectedIndex,
            service = currentState.service,
            creatorName = currentState.profile?.name,
            postId = currentState.postId,
            postTitle = currentState.post?.post?.title,
            thumbnailUrls = thumbnailUrlsMap,
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

    /** Постановка одной загрузки в системный DownloadManager */
    fun download(url: String, fileName: String?) {
        viewModelScope.launch {
            enqueueDownload(url = url, fileName = fileName)
        }
    }

    /** Массовая загрузка всех доступных вложений/медиа поста */
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

    /** Единая точка добавления файла в загрузку + тост-эффект */
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

    /** Переключение блока перевода и запуск перевода в выбранном режиме */
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

    /** Формирует список URL картинок с thumbnail для image-view галереи */
    internal fun collectImageGalleryUrlsWithThumbnails(selectedUrl: String): List<Pair<String, String?>> {
        val imgBaseUrl = domainResolver.imageBaseUrlByService(currentState.service)

        val contentImages = currentState.contentBlocks
            .orEmpty()
            .mapNotNull { block -> (block as? PostBlock.Image)?.url }
            .filter { it.isNotBlank() }
            .map { it to null }

        val previewImages = currentState.post
            ?.previews
            .orEmpty()
            .asSequence()
            .filter { it.type == "thumbnail" }
            .mapNotNull { preview ->
                val fullUrl = buildPreviewFullUrl(preview)
                val thumbnailUrl = buildThumbnailUrl(imgBaseUrl, preview)
                if (fullUrl != null) fullUrl to thumbnailUrl else null
            }
            .toList()

        val mergedMap = linkedMapOf<String, String?>()
        (contentImages + previewImages).forEach { (fullUrl, thumbnailUrl) ->
            when {
                !mergedMap.containsKey(fullUrl) -> mergedMap[fullUrl] = thumbnailUrl
                mergedMap[fullUrl].isNullOrBlank() && !thumbnailUrl.isNullOrBlank() -> {
                    mergedMap[fullUrl] = thumbnailUrl
                }
            }
        }

        val merged = mergedMap.entries.map { it.key to it.value }
        return if (selectedUrl in merged.map { it.first }) {
            merged
        } else {
            val selectedPair = listOf(selectedUrl to null)
            (selectedPair + merged).distinctBy { it.first }
        }
    }

    /** Строит полный URL для thumbnail-превью */
    private fun buildPreviewFullUrl(preview: PreviewDomain): String? {
        val server = preview.server ?: return null
        val path = preview.path ?: return null
        val name = preview.name ?: return null

        val encodedName = URLEncoder.encode(name, "UTF-8")
        return "$server/data$path?f=$encodedName"
    }

    /** Строит thumbnail URL из PreviewDomain */
    private fun buildThumbnailUrl(imgBaseUrl: String, preview: PreviewDomain): String? {
        val path = preview.path ?: return null
        return "$imgBaseUrl/thumbnail/data$path"
    }

    /** Сбрасывает state при переходе на соседний пост */
    private fun resetForNewPost(nextPostId: String) = setState {
        copy(
            postId = nextPostId,
            loading = true,

            sourcePost = null,
            post = null,
            revisionIds = emptyList(),
            selectedRevisionId = null,
            showButtonTranslate = false,
            contentBlocks = null,
            commentDomains = emptyList(),

            translateExpanded = false,
            translateLoading = false,
            translateText = null,
            translateError = null,

            videoInfo = emptyMap(),
            audioInfo = emptyMap(),

            shareInProgress = false,
            shareBytesRead = 0L,
            shareTotalBytes = 0L,
        )
    }

    /** Начало шеринга: включаем прогресс-оверлей */
    private fun onShareStarted() = setState {
        if (shareInProgress) return@setState this
        copy(
            shareInProgress = true,
            shareBytesRead = 0L,
            shareTotalBytes = 0L,
        )
    }

    /** Обновление прогресса шеринга */
    private fun onShareProgress(bytesRead: Long, totalBytes: Long) = setState {
        if (!shareInProgress) return@setState this
        copy(
            shareBytesRead = bytesRead,
            shareTotalBytes = totalBytes,
        )
    }

    /** Завершение шеринга: скрываем прогресс-оверлей */
    private fun onShareFinished() = setState {
        if (!shareInProgress) return@setState this
        copy(shareInProgress = false)
    }

}
