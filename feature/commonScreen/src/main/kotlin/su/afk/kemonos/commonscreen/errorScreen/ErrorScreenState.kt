package su.afk.kemonos.commonscreen.errorScreen

import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.models.ErrorItem

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
