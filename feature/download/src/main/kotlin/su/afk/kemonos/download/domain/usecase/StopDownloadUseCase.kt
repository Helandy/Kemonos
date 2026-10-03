package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.download.domain.model.DownloadStatusCode
import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import javax.inject.Inject

internal class StopDownloadUseCase @Inject constructor(
    private val downloadManagerDataSource: DownloadManagerDataSource,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(downloadId: Long) {
        downloadManagerDataSource.remove(downloadId)
        trackedDownloadsRepository.updateRuntimeState(
            downloadId = downloadId,
            lastStatus = DownloadStatusCode.PAUSED,
            lastReason = DownloadStatusCode.PAUSED_UNKNOWN,
            lastErrorLabel = USER_STOPPED_LABEL,
            lastSeenAtMs = System.currentTimeMillis(),
        )
    }
}

internal const val USER_STOPPED_LABEL = "USER_STOPPED"
