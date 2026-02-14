package su.afk.kemonos.download.presenter

import android.app.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.download.data.DownloadManagerDataSource
import su.afk.kemonos.download.presenter.model.DownloadUiItem
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import javax.inject.Inject

@HiltViewModel
internal class DownloadsViewModel @Inject constructor(
    private val downloadManagerDataSource: DownloadManagerDataSource,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
    private val navigationManager: NavigationManager,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<DownloadsState.State, DownloadsState.Event, DownloadsState.Effect>() {
    private val refreshMutex = Mutex()
    private val speedMap = mutableMapOf<Long, SpeedPoint>()
    private var tracked: List<TrackedDownload> = emptyList()

    override fun createInitialState(): DownloadsState.State = DownloadsState.State()

    override fun onEvent(event: DownloadsState.Event) {
        when (event) {
            DownloadsState.Event.BackClick -> navigationManager.back()
        }
    }

    override fun onRetry() {
        refreshNow()
    }

    init {
        viewModelScope.launch {
            trackedDownloadsRepository.observeAll().collect { items ->
                tracked = items
                refreshNow()
            }
        }

        viewModelScope.launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                refreshNow()
            }
        }
    }

    private fun refreshNow() {
        viewModelScope.launch {
            refreshMutex.withLock {
                refreshInternal()
            }
        }
    }

    private fun refreshInternal() {
        if (tracked.isEmpty()) {
            speedMap.clear()
            setState {
                copy(
                    isLoading = false,
                    items = emptyList(),
                    lastUpdatedMs = System.currentTimeMillis(),
                )
            }
            return
        }

        val snapshots = downloadManagerDataSource.querySnapshots(tracked.map { it.downloadId })
        val nowMs = System.currentTimeMillis()
        speedMap.keys.retainAll(tracked.map { it.downloadId }.toSet())

        val uiItems = tracked.map { item ->
            val snapshot = snapshots[item.downloadId]
            val speed = calcSpeed(item.downloadId, snapshot, nowMs)
            DownloadUiItem.from(
                tracked = item,
                snapshot = snapshot,
                speedBytesPerSec = speed,
            )
        }

        setState {
            copy(
                isLoading = false,
                items = uiItems,
                lastUpdatedMs = nowMs,
            )
        }
    }

    private fun calcSpeed(id: Long, snapshot: DownloadSnapshot?, nowMs: Long): Long {
        if (snapshot == null) return 0L
        val prev = speedMap[id]
        val currentBytes = snapshot.bytesDownloaded.coerceAtLeast(0L)
        speedMap[id] = SpeedPoint(bytes = currentBytes, timestampMs = nowMs)

        if (snapshot.status != DownloadManager.STATUS_RUNNING || prev == null) return 0L
        val dt = nowMs - prev.timestampMs
        if (dt <= 0L) return 0L
        val db = (currentBytes - prev.bytes).coerceAtLeast(0L)
        return (db * 1000L) / dt
    }
}

private typealias DownloadSnapshot = su.afk.kemonos.download.data.DownloadManagerSnapshot

private data class SpeedPoint(
    val bytes: Long,
    val timestampMs: Long,
)

private const val POLL_INTERVAL_MS = 1000L
