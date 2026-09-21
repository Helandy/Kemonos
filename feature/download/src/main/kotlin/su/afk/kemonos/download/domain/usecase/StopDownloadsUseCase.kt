package su.afk.kemonos.download.domain.usecase

import javax.inject.Inject

internal class StopDownloadsUseCase @Inject constructor(
    private val stopDownloadUseCase: StopDownloadUseCase,
) {
    suspend operator fun invoke(downloadIds: List<Long>) {
        downloadIds.forEach { downloadId -> stopDownloadUseCase(downloadId) }
    }
}
