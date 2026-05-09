package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal class DeleteDownloadsUseCase @Inject constructor(
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(downloadIds: List<Long>) = withIo {
        downloadIds.forEach { downloadId ->
            trackedDownloadsRepository.delete(downloadId)
        }
    }
}
