package su.afk.kemonos.creatorPost.presenter

import androidx.lifecycle.SavedStateHandle
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
import su.afk.kemonos.creatorPost.presenter.delegates.MediaMetaDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.NavigateDelegates
import su.afk.kemonos.creatorPost.presenter.delegates.PostDownloadDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.PostLoadDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.TranslateDelegate
import su.afk.kemonos.creatorPost.presenter.helper.ImageGalleryUrlsBuilder
import su.afk.kemonos.creatorPost.presenter.model.LoadRequest
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.error.error.toFavoriteToastBar
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.ui.IUiSettingsReader
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.ui.presenter.baseViewModel.getSerializableState
import su.afk.kemonos.ui.presenter.baseViewModel.setSerializableState
import su.afk.kemonos.ui.shared.ShareLinkBuilder
import su.afk.kemonos.ui.shared.model.ShareTarget

internal class CreatorPostViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorPostDestination.CreatorPost,
    private val postLoadDelegate: PostLoadDelegate,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val mediaMetaDelegate: MediaMetaDelegate,
    private val postDownloadDelegate: PostDownloadDelegate,
    private val translateDelegate: TranslateDelegate,
    private val imageGalleryUrlsBuilder: ImageGalleryUrlsBuilder,
    private val likeDelegate: LikeDelegate,
    private val navigateDelegates: NavigateDelegates,
    private val uiSetting: IUiSettingsReader,
    @Assisted savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<State, Event, Effect>(savedStateHandle) {
    private var loadingJob: Job? = null
    private var loadingRequestId: Long = 0L

    @AssistedFactory
    interface Factory {
        fun create(
            dest: CreatorPostDestination.CreatorPost,
            savedStateHandle: SavedStateHandle,
        ): CreatorPostViewModel
    }

    /** Начальное состояние экрана поста */
    override fun createInitialState(): State =
        (savedStateHandle.getSerializableState<CreatorPostPersistedState>(KEY_STATE)
            ?: CreatorPostPersistedState.fromDest(dest))
            .toState()

    override fun saveToSavedState(state: State) {
        savedStateHandle.setSerializableState(KEY_STATE, state.toPersistedState())
    }

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
        setState { copy(loading = true) }

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
            val restoredRevisionId = currentState.selectedRevisionId
                ?.takeIf { restored -> restored in loaded.revisionIds }
            val resolvedLoaded = if (restoredRevisionId != null) {
                loaded.sourcePost?.let { sourcePost ->
                    val revision = postLoadDelegate.loadRevision(sourcePost, restoredRevisionId)
                    loaded.copy(
                        resolvedPost = revision.resolvedPost,
                        selectedRevisionId = restoredRevisionId,
                        showButtonTranslate = revision.showButtonTranslate,
                        contentBlocks = revision.contentBlocks,
                    )
                } ?: loaded
            } else {
                loaded
            }

            setState {
                copy(
                    loading = false,
                    sourcePost = resolvedLoaded.sourcePost,
                    post = resolvedLoaded.resolvedPost,
                    revisionIds = resolvedLoaded.revisionIds,
                    selectedRevisionId = resolvedLoaded.selectedRevisionId,
                    showButtonTranslate = resolvedLoaded.showButtonTranslate,
                    contentBlocks = resolvedLoaded.contentBlocks,
                    commentDomains = resolvedLoaded.comments,
                    profile = resolvedLoaded.profile,

                    translateExpanded = translateExpanded && resolvedLoaded.showButtonTranslate,
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

    /** Обновляет состояние избранного/лайка (кнопка доступна всегда, авторизация не требуется) */
    private suspend fun applyFavoriteState(request: LoadRequest) {
        val favorite = likeDelegate.isPostFavorite(
            service = request.service,
            creatorId = request.creatorId,
            postId = request.postId,
        )
        if (!isLatestRequest(request)) return

        setState {
            copy(
                isFavoriteShowButton = true,
                isFavorite = favorite
            )
        }
    }

    /** Запрашивает мета-информацию видео */
    private fun requestVideoMeta(server: String, path: String) = requestMediaMeta(
        path = path,
        infoState = { it.videoInfo[path] },
        updateInfo = { info -> copy(videoInfo = videoInfo + (path to info)) },
        load = { useExternalMetaData, service ->
            mediaMetaDelegate.getVideoInfoWithFallback(
                useExternalMetaData = useExternalMetaData,
                service = service,
                server = server,
                path = path,
            )
        },
    )

    /** Запрашивает мета-информацию аудио */
    private fun requestAudioMeta(server: String?, path: String) = requestMediaMeta(
        path = path,
        infoState = { it.audioInfo[path] },
        updateInfo = { info -> copy(audioInfo = audioInfo + (path to info)) },
        load = { useExternalMetaData, service ->
            mediaMetaDelegate.getAudioInfoWithFallback(
                useExternalMetaData = useExternalMetaData,
                service = service,
                server = server,
                path = path,
            )
        },
    )

    private fun requestMediaMeta(
        path: String,
        infoState: (State) -> MediaInfoState?,
        updateInfo: State.(MediaInfoState) -> State,
        load: suspend (useExternalMetaData: Boolean, service: String) -> CommonMediaInfo,
    ) = viewModelScope.launch {
        val current = infoState(currentState)
        if (current is MediaInfoState.Success || current is MediaInfoState.Loading) return@launch
        val settings = currentState.uiSettingModel
        val useExternalMetaData = settings.videoPreviewServerUrl.isNotBlank() && settings.useExternalMetaData

        setState { updateInfo(MediaInfoState.Loading) }

        runCatching { load(useExternalMetaData, currentState.service) }
            .onSuccess { result -> setState { updateInfo(MediaInfoState.Success(result)) } }
            .onFailure { error -> setState { updateInfo(MediaInfoState.Error(error)) } }
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
        val imageUrlsWithThumbnails = imageGalleryUrlsBuilder.build(
            service = currentState.service,
            contentBlocks = currentState.contentBlocks,
            previews = currentState.post?.previews,
            selectedUrl = originalUrl,
        )
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
            postDownloadDelegate.download(url, fileName, currentState, ::setEffect)
        }
    }

    /** Массовая загрузка всех доступных вложений/медиа поста */
    private fun downloadAll() {
        viewModelScope.launch {
            postDownloadDelegate.downloadAll(currentState, ::setEffect)
        }
    }

    /** Переключение блока перевода и запуск перевода в выбранном режиме */
    fun onToggleTranslate() = translateDelegate.onToggleTranslate(
        scope = viewModelScope,
        getState = { currentState },
        updateState = { reducer -> setState(reducer) },
        sendEffect = ::setEffect,
    )

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

    private companion object {
        const val KEY_STATE = "creator_post_state"
    }
}
