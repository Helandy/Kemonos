package su.afk.kemonos.storage.entity.popular.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity

@Dao
interface KemonoPostsPopularCacheDao {

    @Query(
        """
        SELECT * FROM posts_popular_cache
        WHERE queryKey = :queryKey AND offset = :offset
        LIMIT 1
        """
    )
    suspend fun get(queryKey: String, offset: Int): PostsPopularCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PostsPopularCacheEntity)

    @Query(
        """
        DELETE FROM posts_popular_cache
        WHERE queryKey = :queryKey AND offset = :offset
        """
    )
    suspend fun delete(queryKey: String, offset: Int)

    @Query("DELETE FROM posts_popular_cache")
    suspend fun clearAll()

    /**
     * Удаляем ВСЁ просроченное по конкретным периодам:
     * period = substringBefore('|') у queryKey вида "RECENT|2025-12-20"
     */
    @Query(
        """
        DELETE FROM posts_popular_cache
        WHERE updatedAt < :minTs
          AND substr(queryKey, 1, instr(queryKey, '|') - 1) IN (:periods)
        """
    )
    suspend fun deleteExpiredByPeriods(minTs: Long, periods: List<String>)

    /**
     * (Опционально) если хочешь чистить вообще всё, не только периодами:
     */
    @Query("DELETE FROM posts_popular_cache WHERE updatedAt < :minTs")
    suspend fun deleteExpiredAll(minTs: Long)
}