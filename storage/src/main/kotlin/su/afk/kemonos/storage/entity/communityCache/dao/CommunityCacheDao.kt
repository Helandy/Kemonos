package su.afk.kemonos.storage.entity.communityCache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType
import su.afk.kemonos.storage.entity.communityCache.CommunityCacheEntity

@Dao
interface CommunityCacheDao {

    @Query(
        """
        SELECT * FROM community_cache
        WHERE service = :service AND id = :id AND type = :type
          AND cachedAt >= :minCachedAt
        LIMIT 1
    """
    )
    suspend fun getFresh(
        service: String,
        id: String,
        type: CommunityCacheType,
        minCachedAt: Long
    ): CommunityCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CommunityCacheEntity)

    @Query("DELETE FROM community_cache")
    suspend fun clearAll()
}
