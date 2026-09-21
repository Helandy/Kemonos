package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import javax.inject.Inject

internal class StopDownloadsUseCase @Inject constructor(
    private val stopDownloadUseCase: StopDownloadUseCase,
) {
    suspend operator fun invoke(downloads: List<TrackedDownload>) {
        for (download in downloads) {
            stopDownloadUseCase(download.downloadId)
        }
    }
}
