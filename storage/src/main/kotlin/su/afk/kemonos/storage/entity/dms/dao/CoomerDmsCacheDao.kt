package su.afk.kemonos.storage.entity.dms.dao

import androidx.room.*
import su.afk.kemonos.storage.entity.dms.entity.DmsCacheEntity

@Dao
interface CoomerDmsCacheDao {

    @Query(
        """
        SELECT * FROM dms_cache
        WHERE queryKey = :queryKey AND offset = :offset
        ORDER BY indexInPage ASC
        """
    )
    suspend fun getPage(queryKey: String, offset: Int): List<DmsCacheEntity>

    @Query(
        """
        SELECT * FROM dms_cache
        WHERE queryKey = :queryKey
          AND offset = :offset
          AND updatedAt >= :minUpdatedAt
        ORDER BY indexInPage ASC
        """
    )
    suspend fun getFreshPage(
        queryKey: String,
        offset: Int,
        minUpdatedAt: Long,
    ): List<DmsCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<DmsCacheEntity>)

    @Query("DELETE FROM dms_cache WHERE queryKey = :queryKey AND offset = :offset")
    suspend fun clearPage(queryKey: String, offset: Int)

    @Transaction
    suspend fun replacePage(queryKey: String, offset: Int, items: List<DmsCacheEntity>) {
        clearPage(queryKey, offset)
        if (items.isNotEmpty()) upsertAll(items)
    }

    @Query("DELETE FROM dms_cache WHERE updatedAt < :minUpdatedAt")
    suspend fun deleteOlderThan(minUpdatedAt: Long)

    @Query("DELETE FROM dms_cache")
    suspend fun clearAll()
}
