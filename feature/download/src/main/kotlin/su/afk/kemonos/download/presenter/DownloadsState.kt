package su.afk.kemonos.download.presenter

import android.app.DownloadManager
import su.afk.kemonos.download.presenter.model.DownloadUiItem
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class DownloadsState {
    data class State(
        val isLoading: Boolean = true,
        val items: List<DownloadUiItem> = emptyList(),
        val lastUpdatedMs: Long = 0L,
        val selectedFilter: DownloadStatusFilter = DownloadStatusFilter.ALL,
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object BackClick : Event
        data class SelectFilter(val filter: DownloadStatusFilter) : Event
        data class PauseDownload(val downloadId: Long) : Event
        data class StartDownload(val downloadId: Long) : Event
        data class StopDownload(val downloadId: Long) : Event
        data class RestartDownload(val downloadId: Long) : Event
    }
}

internal enum class DownloadStatusFilter {
    ALL,
    RUNNING,
    COMPLETED,
    FAILED,
    STOPPED,
    PENDING,
    PAUSED,
    ;

    fun matches(status: Int): Boolean = when (this) {
        ALL -> true
        PENDING -> status == DownloadManager.STATUS_PENDING
        RUNNING -> status == DownloadManager.STATUS_RUNNING
        PAUSED -> status == DownloadManager.STATUS_PAUSED
        COMPLETED -> status == DownloadManager.STATUS_SUCCESSFUL
        FAILED -> status == DownloadManager.STATUS_FAILED
        STOPPED -> status == DownloadUiItem.STATUS_REMOVED
    }
}
