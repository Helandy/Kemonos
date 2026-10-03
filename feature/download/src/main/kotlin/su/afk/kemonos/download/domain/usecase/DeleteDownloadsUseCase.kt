package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import javax.inject.Inject

internal class DeleteDownloadsUseCase @Inject constructor(
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(downloadIds: List<Long>) {
        downloadIds.forEach { downloadId ->
            trackedDownloadsRepository.delete(downloadId)
        }
    }
}
