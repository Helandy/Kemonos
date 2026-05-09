package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Effect
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Event
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.State
import su.afk.kemonos.commonscreen.navigator.CommonScreenDestination
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.imageLoader.imageProgress.ImageProgressStore
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.presenter.baseViewModel.getSerializableState
import su.afk.kemonos.ui.presenter.baseViewModel.setSerializableState
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

/** Отдельный экран для просмотра картинки */
@OptIn(FlowPreview::class)
internal class ImageViewViewModel @AssistedInject constructor(
    @Assisted private val dest: CommonScreenDestination.ImageViewDest,
    private val navManager: NavigationManager,
    private val progressStore: ImageProgressStore,
    private val downloadUtil: IDownloadUtil,
    private val uiSettingUseCase: IUiSettingUseCase,
    @Assisted savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModelNew<State, Event, Effect>(savedStateHandle) {

    @AssistedFactory
    interface Factory {
        fun create(
            dest: CommonScreenDestination.ImageViewDest,
            savedStateHandle: SavedStateHandle,
        ): ImageViewViewModel
    }

    override fun createInitialState(): State =
        createStateFromArgs(
            args = dest.args,
            persistedState = savedStateHandle.getSerializableState(KEY_STATE),
        )

    override fun saveToSavedState(state: State) {
        savedStateHandle.setSerializableState(KEY_STATE, state.toPersistedState())
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> {
                progressStore.clear(state.value.requestId)
                navManager.back()
            }

            Event.PrevImage -> openRelativeImage(offset = -1)
            Event.NextImage -> openRelativeImage(offset = 1)
            Event.DownloadCurrentImage -> downloadCurrentImage()
            Event.CopyCurrentImageLink -> {
                state.value.imageUrl
                    ?.takeIf { it.isNotBlank() }
                    ?.let { setEffect(Effect.CopyUrl(it)) }
            }

            Event.ImageLoaded -> {
                progressStore.clear(state.value.requestId)
                setState { copy(loading = false, isLoadError = false, errorItem = null) }
            }

            is Event.ImageFailed -> {
                progressStore.clear(state.value.requestId)
                val throwable = event.throwable ?: IllegalStateException("Image loading failed")
                val parsed = errorHandler.parse(throwable, navigate = false)
                val errorWithUrl = parsed.copy(url = parsed.url ?: state.value.imageUrl)
                setState {
                    copy(
                        loading = false,
                        isLoadError = true,
                        errorItem = errorWithUrl,
                    )
                }
            }

            Event.Retry -> retryCurrentImage()
        }
    }

    init {
        observeUiSettings()
        observeProgress()
    }

    private fun observeUiSettings() {
        viewModelScope.launch {
            uiSettingUseCase.prefs
                .map { it.usePreviewOnlyInImageViewer }
                .distinctUntilChanged()
                .collect { enabled ->
                    val current = state.value
                    if (
                        current.imageViewerSettingsLoaded &&
                        current.usePreviewOnlyInImageViewer == enabled
                    ) {
                        return@collect
                    }

                    progressStore.clear(current.requestId)
                    setState {
                        copy(
                            usePreviewOnlyInImageViewer = enabled,
                            imageViewerSettingsLoaded = true,
                            requestId = newRequestId(),
                            loading = true,
                            isLoadError = false,
                            errorItem = null,
                            bytesRead = 0L,
                            contentLength = -1L,
                            progress = 0f,
                        )
                    }
                }
        }
    }

    private fun observeProgress() {
        viewModelScope.launch {
            state.map { it.requestId }
                .distinctUntilChanged()
                .collectLatest { key ->
                    progressStore.flowFor(key)
                        .filterNotNull()
                        .sample(100.milliseconds)
                        .map { p ->
                            val percent = (p.percent ?: 0f).coerceIn(0f, 1f)
                            // округляем, чтобы distinct работал (например до 0.5%)
                            val percentStep = (percent * 200).toInt() // 200 шагов = 0.5%
                            val kbStep = (p.bytesRead / 16_384) // шаг 16KB
                            Triple(p, percentStep, kbStep)
                        }
                        .distinctUntilChanged { old, new ->
                            old.second == new.second && old.third == new.third
                        }
                        .collect { (p, _, _) ->
                            val percent = (p.percent ?: 0f).coerceIn(0f, 1f)
                            setState {
                                copy(
                                    bytesRead = p.bytesRead,
                                    contentLength = p.contentLength,
                                    progress = percent
                                )
                            }
                        }
                }
        }
    }

    private fun retryCurrentImage() {
        progressStore.clear(state.value.requestId)

        setState {
            copy(
                requestId = newRequestId(),
                loading = true,
                isLoadError = false,
                reloadKey = reloadKey + 1,
                bytesRead = 0L,
                contentLength = -1L,
                progress = 0f,
            )
        }
    }

    private fun openRelativeImage(offset: Int) {
        val urls = state.value.imageUrls
        if (urls.size <= 1) return

        val currentIndex = state.value.selectedIndex
        val targetIndex = (currentIndex + offset).coerceIn(0, urls.lastIndex)
        if (targetIndex == currentIndex) return

        progressStore.clear(state.value.requestId)
        setState {
            copy(
                imageUrl = urls[targetIndex],
                selectedIndex = targetIndex,
                requestId = newRequestId(),
                reloadKey = 0,
                loading = true,
                isLoadError = false,
                errorItem = null,
                bytesRead = 0L,
                contentLength = -1L,
                progress = 0f,
            )
        }
    }

    private fun downloadCurrentImage() {
        val url = state.value.imageUrl
            ?.takeIf { it.isNotBlank() }
            ?: return
        val fileName = url.toUri().lastPathSegment
            ?.takeIf { it.isNotBlank() }
            ?: url.substringAfterLast('/').substringBefore('?').ifBlank { "image" }

        viewModelScope.launch {
            runCatching {
                downloadUtil.enqueueSystemDownload(
                    url = url,
                    fileName = fileName,
                    service = state.value.service,
                    creatorName = state.value.creatorName,
                    postId = state.value.postId,
                    postTitle = state.value.postTitle,
                )
            }.onSuccess {
                setEffect(Effect.DownloadToast(fileName))
            }.onFailure { t ->
                setEffect(Effect.ShowToast(errorHandler.parse(t, navigate = false).message))
            }
        }
    }

    private fun createStateFromArgs(
        args: CommonScreenDestination.ImageViewArgs,
        persistedState: ImageViewPersistedState?,
    ): State {
        val preparedUrls = prepareImageUrls(imageUrl = args.imageUrl, imageUrls = args.imageUrls)
        val selectedIndex = persistedState?.selectedIndex ?: args.selectedIndex
        val safeSelectedIndex = selectedIndex
            .takeIf { it in preparedUrls.indices }
            ?: preparedUrls.indexOf(args.imageUrl).takeIf { idx -> idx >= 0 }
            ?: 0
        val initialUrl = preparedUrls.getOrNull(safeSelectedIndex) ?: args.imageUrl

        return State(
            imageUrl = initialUrl,
            imageUrls = preparedUrls,
            selectedIndex = safeSelectedIndex,
            service = args.service,
            creatorName = args.creatorName,
            postId = args.postId,
            postTitle = args.postTitle,
            thumbnailUrls = args.thumbnailUrls,
            requestId = newRequestId(),
            reloadKey = persistedState?.reloadKey ?: 0,
            loading = true,
        )
    }

    private fun prepareImageUrls(imageUrl: String?, imageUrls: List<String>): List<String> {
        val sanitizedUrls = imageUrls.filter { it.isNotBlank() }

        return when {
            sanitizedUrls.isEmpty() && imageUrl.isNullOrBlank() -> emptyList()
            sanitizedUrls.isEmpty() -> listOf(imageUrl.orEmpty())
            imageUrl.isNullOrBlank() -> sanitizedUrls.distinct()
            imageUrl in sanitizedUrls -> sanitizedUrls.distinct()
            else -> (listOf(imageUrl) + sanitizedUrls).distinct()
        }
    }

    private fun newRequestId(): String = UUID.randomUUID().toString()

    private companion object {
        const val KEY_STATE = "image_view_state"
    }
}
