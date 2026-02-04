package su.afk.kemonos.commonscreen.imageViewScreen

import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState

internal class ImageViewState {

    data class State(
        val loading: Boolean = true,
        val imageUrl: String? = null,
    ) : UiState

    sealed class Event : UiEvent {
        object Back : Event()
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
    }
}