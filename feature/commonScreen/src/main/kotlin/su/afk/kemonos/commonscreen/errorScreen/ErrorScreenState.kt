package su.afk.kemonos.commonscreen.errorScreen

import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class ErrorScreenState {

    data class State(
        val error: ErrorItem? = null,
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data object Back : Event
    }

    sealed interface Effect : UiEffect
}
