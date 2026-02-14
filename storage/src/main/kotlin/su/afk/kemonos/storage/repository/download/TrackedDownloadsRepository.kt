package su.afk.kemonos.storage.repository.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import su.afk.kemonos.storage.entity.download.DownloadTaskEntity
import su.afk.kemonos.storage.entity.download.dao.DownloadTaskDao
import javax.inject.Inject

internal class TrackedDownloadsRepository @Inject constructor(
    private val dao: DownloadTaskDao,
) : ITrackedDownloadsRepository {
    override fun observeAll(): Flow<List<TrackedDownload>> =
        dao.observeAll().map { items -> items.map(::toDomain) }

    override suspend fun upsert(item: TrackedDownload) {
        dao.upsert(item.toEntity())
    }
}

private fun toDomain(item: DownloadTaskEntity): TrackedDownload =
    TrackedDownload(
        downloadId = item.downloadId,
        url = item.url,
        fileName = item.fileName,
        service = item.service,
        creatorName = item.creatorName,
        postId = item.postId,
        postTitle = item.postTitle,
        createdAtMs = item.createdAtMs,
    )

private fun TrackedDownload.toEntity(): DownloadTaskEntity =
    DownloadTaskEntity(
        downloadId = downloadId,
        url = url,
        fileName = fileName,
        service = service,
        creatorName = creatorName,
        postId = postId,
        postTitle = postTitle,
        createdAtMs = createdAtMs,
    )
