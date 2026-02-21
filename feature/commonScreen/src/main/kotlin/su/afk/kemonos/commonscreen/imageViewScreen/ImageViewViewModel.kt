package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.core.net.toUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.*
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_CREATOR_NAME
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_IMAGE_URLS
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_POST_ID
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_POST_TITLE
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SELECTED_IMAGE
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SELECTED_IMAGE_INDEX
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SERVICE
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.ui.imageLoader.imageProgress.ImageProgressStore
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/** Отдельный экран для просмотра картинки */
@OptIn(FlowPreview::class)
@HiltViewModel
internal class ImageViewViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val progressStore: ImageProgressStore,
    private val downloadUtil: IDownloadUtil,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

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
        val imageUrl = navigationStorage.consume<String>(KEY_SELECTED_IMAGE)
        val imageUrls = navigationStorage.consume<List<String>>(KEY_IMAGE_URLS).orEmpty()
        val selectedIndex = navigationStorage.consume<Int>(KEY_SELECTED_IMAGE_INDEX)
        val service = navigationStorage.consume<String>(KEY_SERVICE)
        val creatorName = navigationStorage.consume<String>(KEY_CREATOR_NAME)
        val postId = navigationStorage.consume<String>(KEY_POST_ID)
        val postTitle = navigationStorage.consume<String>(KEY_POST_TITLE)

        val preparedUrls = prepareImageUrls(imageUrl = imageUrl, imageUrls = imageUrls)
        val safeSelectedIndex = selectedIndex
            ?.takeIf { it in preparedUrls.indices }
            ?: imageUrl
                ?.let { selected -> preparedUrls.indexOf(selected).takeIf { idx -> idx >= 0 } }
            ?: 0
        val initialUrl = preparedUrls.getOrNull(safeSelectedIndex) ?: imageUrl

        setState {
            copy(
                imageUrl = initialUrl,
                imageUrls = preparedUrls,
                selectedIndex = safeSelectedIndex,
                service = service,
                creatorName = creatorName,
                postId = postId,
                postTitle = postTitle,
                requestId = newRequestId(),
                loading = true,
                isLoadError = false,
                errorItem = null,
                bytesRead = 0L,
                contentLength = -1L,
                progress = 0f,
            )
        }

        observeProgress()
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
}
