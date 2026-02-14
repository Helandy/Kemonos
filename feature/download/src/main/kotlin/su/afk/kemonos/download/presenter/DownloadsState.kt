package su.afk.kemonos.download.presenter

import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.download.presenter.model.DownloadUiItem

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
