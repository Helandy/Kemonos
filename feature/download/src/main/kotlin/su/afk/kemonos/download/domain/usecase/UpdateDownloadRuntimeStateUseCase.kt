package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import javax.inject.Inject

internal class UpdateDownloadRuntimeStateUseCase @Inject constructor(
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(
        downloadId: Long,
        lastStatus: Int?,
        lastReason: Int?,
        lastErrorLabel: String?,
        lastSeenAtMs: Long?,
    ) = trackedDownloadsRepository.updateRuntimeState(
        downloadId = downloadId,
        lastStatus = lastStatus,
        lastReason = lastReason,
        lastErrorLabel = lastErrorLabel,
        lastSeenAtMs = lastSeenAtMs,
    )
}
