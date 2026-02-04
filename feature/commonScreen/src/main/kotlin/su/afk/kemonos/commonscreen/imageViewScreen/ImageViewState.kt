package su.afk.kemonos.commonscreen.imageViewScreen

import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState

internal class ImageViewState {

    data class State(
        val loading: Boolean = true,
        val imageUrl: String? = null,

        val requestId: String = java.util.UUID.randomUUID().toString(),

        val bytesRead: Long = 0L,
        val contentLength: Long = -1L,
        val progress: Float = 0f,
    ) : UiState

    sealed class Event : UiEvent {
        object Back : Event()
        data object ImageLoaded : Event()
        data object ImageFailed : Event()
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
    }
}