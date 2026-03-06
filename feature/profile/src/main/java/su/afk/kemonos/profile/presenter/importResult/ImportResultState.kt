package su.afk.kemonos.profile.presenter.importResult

import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class ImportResultState {
    data class State(
        val payload: ImportResultPayload? = null,
    ) : UiState

    sealed interface Event : UiEvent {
        data object Back : Event
    }

    sealed interface Effect : UiEffect
}
