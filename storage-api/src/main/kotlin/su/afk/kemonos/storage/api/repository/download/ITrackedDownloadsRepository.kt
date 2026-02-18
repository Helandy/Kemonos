package su.afk.kemonos.storage.api.repository.download

import kotlinx.coroutines.flow.Flow

interface ITrackedDownloadsRepository {
    fun observeAll(): Flow<List<TrackedDownload>>

    suspend fun upsert(item: TrackedDownload)

    suspend fun delete(downloadId: Long)

    suspend fun clearCache()

    suspend fun updateRuntimeState(
        downloadId: Long,
        lastStatus: Int?,
        lastReason: Int?,
        lastErrorLabel: String?,
        lastSeenAtMs: Long?,
    )
}
