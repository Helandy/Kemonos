package su.afk.kemonos.storage.entity.download.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.entity.download.DownloadTaskEntity

@Dao
internal interface DownloadTaskDao {
    @Query("SELECT * FROM tracked_downloads ORDER BY createdAtMs DESC")
    fun observeAll(): Flow<List<DownloadTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: DownloadTaskEntity)

    @Query("DELETE FROM tracked_downloads WHERE downloadId = :downloadId")
    suspend fun delete(downloadId: Long)

    @Query("DELETE FROM tracked_downloads WHERE createdAtMs < :thresholdMs")
    suspend fun clearOlderThan(thresholdMs: Long)

    @Query(
        """
        UPDATE tracked_downloads
        SET lastStatus = :lastStatus,
            lastReason = :lastReason,
            lastErrorLabel = :lastErrorLabel,
            lastSeenAtMs = :lastSeenAtMs
        WHERE downloadId = :downloadId
        """
    )
    suspend fun updateRuntimeState(
        downloadId: Long,
        lastStatus: Int?,
        lastReason: Int?,
        lastErrorLabel: String?,
        lastSeenAtMs: Long?,
    )
}
