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
}
