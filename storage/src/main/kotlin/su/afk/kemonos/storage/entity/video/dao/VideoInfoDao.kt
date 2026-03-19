package su.afk.kemonos.storage.entity.video.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.entity.video.VideoInfoEntity

@Dao
interface VideoInfoDao {

    @Query("SELECT * FROM video_info WHERE site = :site AND path = :path LIMIT 1")
    suspend fun get(site: SelectedSite, path: String): VideoInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: VideoInfoEntity)

    @Query("DELETE FROM video_info WHERE createdAt < :expireBefore")
    suspend fun clearExpired(expireBefore: Long)

    @Query("DELETE FROM video_info")
    suspend fun clear()
}
