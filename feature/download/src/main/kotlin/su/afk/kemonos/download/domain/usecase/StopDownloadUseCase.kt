package su.afk.kemonos.download.domain.usecase

import android.app.DownloadManager
import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal class StopDownloadUseCase @Inject constructor(
    private val downloadManagerDataSource: DownloadManagerDataSource,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(downloadId: Long) = withIo {
        downloadManagerDataSource.remove(downloadId)
        trackedDownloadsRepository.updateRuntimeState(
            downloadId = downloadId,
            lastStatus = DownloadManager.STATUS_PAUSED,
            lastReason = DownloadManager.PAUSED_UNKNOWN,
            lastErrorLabel = USER_STOPPED_LABEL,
            lastSeenAtMs = System.currentTimeMillis(),
        )
    }
}

internal const val USER_STOPPED_LABEL = "USER_STOPPED"
