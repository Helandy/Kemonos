package su.afk.kemonos.storage.entity.download.mapper

import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import su.afk.kemonos.storage.entity.download.DownloadTaskEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TrackedDownloadMapper @Inject constructor() {
    fun toDomain(entity: DownloadTaskEntity): TrackedDownload =
        TrackedDownload(
            downloadId = entity.downloadId,
            url = entity.url,
            fileName = entity.fileName,
            service = entity.service,
            creatorName = entity.creatorName,
            postId = entity.postId,
            postTitle = entity.postTitle,
            createdAtMs = entity.createdAtMs,
            lastStatus = entity.lastStatus,
            lastReason = entity.lastReason,
            lastErrorLabel = entity.lastErrorLabel,
            lastSeenAtMs = entity.lastSeenAtMs,
        )

    fun toEntity(domain: TrackedDownload): DownloadTaskEntity =
        DownloadTaskEntity(
            downloadId = domain.downloadId,
            url = domain.url,
            fileName = domain.fileName,
            service = domain.service,
            creatorName = domain.creatorName,
            postId = domain.postId,
            postTitle = domain.postTitle,
            createdAtMs = domain.createdAtMs,
            lastStatus = domain.lastStatus,
            lastReason = domain.lastReason,
            lastErrorLabel = domain.lastErrorLabel,
            lastSeenAtMs = domain.lastSeenAtMs,
        )
}
