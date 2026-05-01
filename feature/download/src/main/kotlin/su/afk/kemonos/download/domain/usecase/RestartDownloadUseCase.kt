package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal class RestartDownloadUseCase @Inject constructor(
    private val downloadManagerDataSource: DownloadManagerDataSource,
    private val downloadUtil: IDownloadUtil,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(trackedDownload: TrackedDownload): Long = withIo {
        downloadManagerDataSource.remove(trackedDownload.downloadId)
        val newId = downloadUtil.enqueueSystemDownload(
            url = trackedDownload.url,
            fileName = trackedDownload.fileName,
            service = trackedDownload.service,
            creatorName = trackedDownload.creatorName,
            postId = trackedDownload.postId,
            postTitle = trackedDownload.postTitle,
        )
        if (newId != trackedDownload.downloadId) {
            trackedDownloadsRepository.delete(trackedDownload.downloadId)
        }
        newId
    }
}
