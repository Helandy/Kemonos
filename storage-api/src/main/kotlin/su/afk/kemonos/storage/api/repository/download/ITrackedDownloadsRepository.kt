package su.afk.kemonos.storage.api.repository.download

import kotlinx.coroutines.flow.Flow

interface ITrackedDownloadsRepository {
    fun observeAll(): Flow<List<TrackedDownload>>

    suspend fun upsert(item: TrackedDownload)
}
