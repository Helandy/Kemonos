package su.afk.kemonos.download.domain.usecase

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import javax.inject.Inject

internal class ObserveTrackedDownloadsUseCase @Inject constructor(
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    operator fun invoke(): Flow<List<TrackedDownload>> = trackedDownloadsRepository.observeAll()
}
