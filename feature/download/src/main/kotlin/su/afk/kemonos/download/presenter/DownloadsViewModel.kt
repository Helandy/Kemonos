package su.afk.kemonos.download.presenter

import android.app.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import su.afk.kemonos.download.data.DownloadManagerDataSource
import su.afk.kemonos.download.presenter.model.DownloadUiItem
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
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
    private val lastSnapshots = mutableMapOf<Long, DownloadSnapshot>()
    private var tracked: List<TrackedDownload> = emptyList()

    override fun createInitialState(): DownloadsState.State = DownloadsState.State()

    override fun onEvent(event: DownloadsState.Event) {
        when (event) {
            DownloadsState.Event.BackClick -> navigationManager.back()
        }
    }

    override fun onRetry() {
        refreshAllNow()
    }

    init {
        viewModelScope.launch {
            trackedDownloadsRepository.observeAll().collect { items ->
                tracked = items
                refreshAllNow()
            }
        }

        viewModelScope.launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                refreshActiveNow()
            }
        }
    }

    private fun refreshAllNow() {
        viewModelScope.launch {
            refreshMutex.withLock {
                refreshInternal(onlyActive = false)
            }
        }
    }

    private fun refreshActiveNow() {
        viewModelScope.launch {
            refreshMutex.withLock {
                refreshInternal(onlyActive = true)
            }
        }
    }

    private suspend fun refreshInternal(onlyActive: Boolean) {
        if (tracked.isEmpty()) {
            speedMap.clear()
            lastSnapshots.clear()
            setState {
                copy(
                    isLoading = false,
                    items = emptyList(),
                    lastUpdatedMs = System.currentTimeMillis(),
                )
            }
            return
        }

        val trackedIds = tracked.map { it.downloadId }
        speedMap.keys.retainAll(trackedIds.toSet())
        lastSnapshots.keys.retainAll(trackedIds.toSet())

        val idsForQuery = if (onlyActive) {
            trackedIds.filter { id -> lastSnapshots[id]?.status.isActiveStatus() }
        } else {
            trackedIds
        }

        val snapshots = if (idsForQuery.isNotEmpty()) {
            downloadManagerDataSource.querySnapshots(idsForQuery)
        } else {
            emptyMap()
        }
        if (snapshots.isNotEmpty()) {
            lastSnapshots.putAll(snapshots)
        }

        val nowMs = System.currentTimeMillis()

        val uiItems = tracked.map { item ->
            val snapshot = lastSnapshots[item.downloadId]
            val speed = calcSpeed(item.downloadId, snapshot, nowMs)
            DownloadUiItem.from(
                tracked = item,
                snapshot = snapshot,
                speedBytesPerSec = speed,
            )
        }

        persistFailedItems(uiItems = uiItems, seenAtMs = nowMs)

        setState {
            copy(
                isLoading = false,
                items = uiItems,
                lastUpdatedMs = nowMs,
            )
        }
    }

    private suspend fun persistFailedItems(
        uiItems: List<DownloadUiItem>,
        seenAtMs: Long,
    ) {
        val trackedById = tracked.associateBy { it.downloadId }
        uiItems.asSequence()
            .filter { it.status == DownloadManager.STATUS_FAILED }
            .forEach { item ->
                val current = trackedById[item.downloadId]
                val statusChanged = current?.lastStatus != item.status
                val reasonChanged = current?.lastReason != item.reasonCode
                val labelChanged = current?.lastErrorLabel != item.reasonLabel

                if (statusChanged || reasonChanged || labelChanged) {
                    trackedDownloadsRepository.updateRuntimeState(
                        downloadId = item.downloadId,
                        lastStatus = item.status,
                        lastReason = item.reasonCode,
                        lastErrorLabel = item.reasonLabel,
                        lastSeenAtMs = seenAtMs,
                    )
                }
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

private fun Int?.isActiveStatus(): Boolean =
    this == DownloadManager.STATUS_PENDING ||
            this == DownloadManager.STATUS_RUNNING ||
            this == DownloadManager.STATUS_PAUSED
