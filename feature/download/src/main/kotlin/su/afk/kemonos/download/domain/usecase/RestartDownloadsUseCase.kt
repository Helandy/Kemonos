package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import javax.inject.Inject

internal class RestartDownloadsUseCase @Inject constructor(
    private val restartDownloadUseCase: RestartDownloadUseCase,
) {
    suspend operator fun invoke(downloads: List<TrackedDownload>): List<Long> {
        downloads.forEach { restartDownloadUseCase(it) }
        return downloads.map { it.downloadId }
    }
}
