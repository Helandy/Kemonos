package su.afk.kemonos.commonscreen.imageViewScreen

import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class ImageViewState {

    data class State(
        val loading: Boolean = true,
        val imageUrl: String? = null,
        val imageUrls: List<String> = emptyList(),
        val selectedIndex: Int = 0,

        val requestId: String = java.util.UUID.randomUUID().toString(),
        val reloadKey: Int = 0,
        val isLoadError: Boolean = false,
        val errorItem: ErrorItem? = null,

        val bytesRead: Long = 0L,
        val contentLength: Long = -1L,
        val progress: Float = 0f,
    ) : UiState

    sealed class Event : UiEvent {
        object Back : Event()
        data object PrevImage : Event()
        data object NextImage : Event()
        data object DownloadCurrentImage : Event()
        data object CopyCurrentImageLink : Event()
        data object ImageLoaded : Event()
        data class ImageFailed(val throwable: Throwable?) : Event()
        data object Retry : Event()
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
        data class ShowToast(val message: String) : Effect
        data class DownloadToast(val fileName: String) : Effect
        data class CopyUrl(val url: String) : Effect
    }
}
