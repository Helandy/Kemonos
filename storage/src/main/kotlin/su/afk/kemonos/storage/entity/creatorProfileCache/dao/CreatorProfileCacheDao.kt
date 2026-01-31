package su.afk.kemonos.storage.entity.creatorProfileCache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.storage.api.repository.creatorProfile.CreatorProfileCacheType
import su.afk.kemonos.storage.entity.creatorProfileCache.CreatorProfileCacheEntity

@Dao
interface CreatorProfileCacheDao {

    @Query(
        """
        SELECT * FROM creator_profile_cache
        WHERE service = :service AND profileId = :profileId AND type = :type
        LIMIT 1
    """
    )
    suspend fun get(
        service: String,
        profileId: String,
        type: CreatorProfileCacheType
    ): CreatorProfileCacheEntity?

    @Query(
        """
        SELECT * FROM creator_profile_cache
        WHERE service = :service AND profileId = :profileId AND type = :type
          AND cachedAt >= :minCachedAt
        LIMIT 1
    """
    )
    suspend fun getFresh(
        service: String,
        profileId: String,
        type: CreatorProfileCacheType,
        minCachedAt: Long
    ): CreatorProfileCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CreatorProfileCacheEntity)

    @Query(
        """
        DELETE FROM creator_profile_cache
        WHERE service = :service AND profileId = :profileId AND type = :type
    """
    )
    suspend fun delete(service: String, profileId: String, type: CreatorProfileCacheType)

    @Query(
        """
        DELETE FROM creator_profile_cache
        WHERE service = :service AND profileId = :profileId
    """
    )
    suspend fun clearProfile(service: String, profileId: String)

    @Query("DELETE FROM creator_profile_cache")
    suspend fun clearAll()

    @Query("DELETE FROM creator_profile_cache WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThan(minCachedAt: Long)
}