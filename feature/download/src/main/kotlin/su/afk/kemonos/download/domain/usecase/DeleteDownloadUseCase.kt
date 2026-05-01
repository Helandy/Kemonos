package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal class DeleteDownloadUseCase @Inject constructor(
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(downloadId: Long) = withIo {
        trackedDownloadsRepository.delete(downloadId)
    }
}
