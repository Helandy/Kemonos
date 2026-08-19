package su.afk.kemonos.storage.entity.popular.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity

@Dao
interface PawchivePostsPopularCacheDao : PostsPopularCacheDao {

    @Query(
        """
        SELECT * FROM posts_popular_cache
        WHERE queryKey = :queryKey AND offset = :offset
        LIMIT 1
        """
    )
    override suspend fun get(queryKey: String, offset: Int): PostsPopularCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsert(entity: PostsPopularCacheEntity)

    @Query("DELETE FROM posts_popular_cache WHERE queryKey = :queryKey AND offset = :offset")
    override suspend fun delete(queryKey: String, offset: Int)

    @Query("DELETE FROM posts_popular_cache")
    override suspend fun clearAll()

    @Query(
        """
        DELETE FROM posts_popular_cache
        WHERE updatedAt < :minTs
          AND substr(queryKey, 1, instr(queryKey, '|') - 1) IN (:periods)
        """
    )
    override suspend fun deleteExpiredByPeriods(minTs: Long, periods: List<String>)

    @Query("DELETE FROM posts_popular_cache WHERE updatedAt < :minTs")
    override suspend fun deleteExpiredAll(minTs: Long)
}
