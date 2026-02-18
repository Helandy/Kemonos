package su.afk.kemonos.download.presenter

import su.afk.kemonos.download.presenter.model.DownloadUiItem
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class DownloadsState {
    data class State(
        val isLoading: Boolean = true,
        val items: List<DownloadUiItem> = emptyList(),
        val lastUpdatedMs: Long = 0L,
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object BackClick : Event
        data class PauseDownload(val downloadId: Long) : Event
        data class StartDownload(val downloadId: Long) : Event
        data class StopDownload(val downloadId: Long) : Event
        data class RestartDownload(val downloadId: Long) : Event
    }

    sealed interface Effect : UiEffect
}
