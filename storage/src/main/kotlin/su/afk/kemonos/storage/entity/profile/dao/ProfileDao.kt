package su.afk.kemonos.storage.entity.profile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.storage.entity.profile.ProfileEntity

@Dao
interface ProfileDao {

    @Query(
        """
        SELECT * FROM profiles
        WHERE id = :id AND service = :service
        LIMIT 1
    """
    )
    suspend fun getProfile(id: String, service: String): ProfileEntity?

    @Query(
        """
        SELECT * FROM profiles
        WHERE id = :id AND service = :service
          AND cachedAt >= :minCachedAt
        LIMIT 1
    """
    )
    suspend fun getFreshProfile(
        id: String,
        service: String,
        minCachedAt: Long
    ): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :id AND service = :service")
    suspend fun deleteProfile(id: String, service: String)

    @Query("DELETE FROM profiles")
    suspend fun clear()

    @Query("DELETE FROM profiles WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThan(minCachedAt: Long)

}
