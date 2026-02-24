package su.afk.kemonos.download.presenter

import android.app.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.download.data.DownloadManagerDataSource
import su.afk.kemonos.download.presenter.model.DownloadUiItem
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import javax.inject.Inject

@HiltViewModel
internal class DownloadsViewModel @Inject constructor(
    private val downloadManagerDataSource: DownloadManagerDataSource,
    private val downloadUtil: IDownloadUtil,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
    private val uiSetting: IUiSettingUseCase,
    private val navigationManager: NavigationManager,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<DownloadsState.State, DownloadsState.Event, UiEffect>() {
    private val refreshMutex = Mutex()
    private val speedMap = mutableMapOf<Long, SpeedPoint>()
    private val lastSnapshots = mutableMapOf<Long, DownloadSnapshot>()
    private var tracked: List<TrackedDownload> = emptyList()

    override fun createInitialState(): DownloadsState.State = DownloadsState.State()

    override fun onEvent(event: DownloadsState.Event) {
        when (event) {
            DownloadsState.Event.BackClick -> navigationManager.back()
            is DownloadsState.Event.SelectFilter -> setState { copy(selectedFilter = event.filter) }
            is DownloadsState.Event.PauseDownload -> pauseDownload(event.downloadId)
            is DownloadsState.Event.StartDownload -> startDownload(event.downloadId)
            is DownloadsState.Event.StopDownload -> stopDownload(event.downloadId)
            is DownloadsState.Event.RestartDownload -> restartDownload(event.downloadId)
            is DownloadsState.Event.DeleteDownload -> deleteDownload(event.downloadId)
        }
    }

    override fun onRetry() {
        refreshAllNow()
    }

    init {
        observeUiSetting()

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

    private fun observeUiSetting() {
        uiSetting.prefs
            .distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshAllNow() {
        viewModelScope.launch {
            refreshMutex.withLock {
                refreshInternal(onlyActive = false)
            }
        }
    }

    private fun pauseDownload(downloadId: Long) {
        viewModelScope.launch {
            refreshMutex.withLock {
                downloadManagerDataSource.remove(downloadId)
                speedMap.remove(downloadId)
                lastSnapshots.remove(downloadId)
                trackedDownloadsRepository.updateRuntimeState(
                    downloadId = downloadId,
                    lastStatus = DownloadManager.STATUS_PAUSED,
                    lastReason = DownloadManager.PAUSED_UNKNOWN,
                    lastErrorLabel = "Paused by user",
                    lastSeenAtMs = System.currentTimeMillis(),
                )
                refreshInternal(onlyActive = false)
            }
        }
    }

    private fun startDownload(downloadId: Long) {
        reEnqueueDownload(downloadId = downloadId, force = false)
    }

    private fun restartDownload(downloadId: Long) {
        reEnqueueDownload(downloadId = downloadId, force = true)
    }

    private fun stopDownload(downloadId: Long) {
        viewModelScope.launch {
            refreshMutex.withLock {
                downloadManagerDataSource.remove(downloadId)
                speedMap.remove(downloadId)
                lastSnapshots.remove(downloadId)
                trackedDownloadsRepository.updateRuntimeState(
                    downloadId = downloadId,
                    lastStatus = DownloadManager.STATUS_PAUSED,
                    lastReason = DownloadManager.PAUSED_UNKNOWN,
                    lastErrorLabel = "Stopped by user",
                    lastSeenAtMs = System.currentTimeMillis(),
                )
                refreshInternal(onlyActive = false)
            }
        }
    }

    private fun reEnqueueDownload(downloadId: Long, force: Boolean) {
        viewModelScope.launch {
            refreshMutex.withLock {
                val trackedItem = tracked.firstOrNull { it.downloadId == downloadId } ?: return@withLock
                val status = lastSnapshots[downloadId]?.status
                val isActive = status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING
                if (!force && isActive) return@withLock

                downloadManagerDataSource.remove(downloadId)
                val newId = downloadUtil.enqueueSystemDownload(
                    url = trackedItem.url,
                    fileName = trackedItem.fileName,
                    service = trackedItem.service,
                    creatorName = trackedItem.creatorName,
                    postId = trackedItem.postId,
                    postTitle = trackedItem.postTitle,
                )
                if (newId != downloadId) {
                    trackedDownloadsRepository.delete(downloadId)
                }

                speedMap.remove(downloadId)
                lastSnapshots.remove(downloadId)
                refreshInternal(onlyActive = false)
            }
        }
    }

    private fun deleteDownload(downloadId: Long) {
        viewModelScope.launch {
            refreshMutex.withLock {
                trackedDownloadsRepository.delete(downloadId)
                speedMap.remove(downloadId)
                lastSnapshots.remove(downloadId)
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
