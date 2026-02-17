package su.afk.kemonos.download.presenter

import su.afk.kemonos.download.presenter.model.DownloadUiItem
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class DownloadsState {
    data class State(
        val isLoading: Boolean = true,
        val items: List<DownloadUiItem> = emptyList(),
        val lastUpdatedMs: Long = 0L,
    ) : UiState

    sealed interface Event : UiEvent {
        data object BackClick : Event
    }

    sealed interface Effect : UiEffect
}
