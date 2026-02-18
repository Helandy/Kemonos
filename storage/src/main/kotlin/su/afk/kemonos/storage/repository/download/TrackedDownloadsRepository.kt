package su.afk.kemonos.storage.repository.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_60_DAYS
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload
import su.afk.kemonos.storage.entity.download.dao.DownloadTaskDao
import su.afk.kemonos.storage.entity.download.mapper.TrackedDownloadMapper
import javax.inject.Inject

internal class TrackedDownloadsRepository @Inject constructor(
    private val dao: DownloadTaskDao,
    private val mapper: TrackedDownloadMapper,
) : ITrackedDownloadsRepository {
    override fun observeAll(): Flow<List<TrackedDownload>> =
        dao.observeAll().map { items -> items.map(mapper::toDomain) }

    override suspend fun upsert(item: TrackedDownload) {
        dao.upsert(mapper.toEntity(item))
    }

    override suspend fun delete(downloadId: Long) {
        dao.delete(downloadId)
    }

    override suspend fun clearCache() {
        val oldDate = System.currentTimeMillis() - TTL_60_DAYS
        dao.clearOlderThan(oldDate)
    }

    override suspend fun updateRuntimeState(
        downloadId: Long,
        lastStatus: Int?,
        lastReason: Int?,
        lastErrorLabel: String?,
        lastSeenAtMs: Long?,
    ) {
        dao.updateRuntimeState(
            downloadId = downloadId,
            lastStatus = lastStatus,
            lastReason = lastReason,
            lastErrorLabel = lastErrorLabel,
            lastSeenAtMs = lastSeenAtMs,
        )
    }
}
