package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal class CancelDownloadsUseCase @Inject constructor(
    private val stopDownloadsUseCase: StopDownloadsUseCase,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(downloads: List<TrackedDownload>) = withIo {
        stopDownloadsUseCase(downloads)
        downloads.forEach { trackedDownloadsRepository.delete(it.downloadId) }
    }
}
