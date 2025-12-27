package su.afk.kemonos.storage.entity.post.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.storage.entity.post.PostContentCacheEntity

@Dao
interface PostContentCacheDao {

    @Query(
        """
        SELECT * FROM post_content_cache
        WHERE service = :service AND userId = :userId AND postId = :postId
        LIMIT 1
    """
    )
    suspend fun get(service: String, userId: String, postId: String): PostContentCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PostContentCacheEntity)

    @Query(
        """
        SELECT * FROM post_content_cache
        WHERE service = :service AND userId = :userId AND postId = :postId
          AND cachedAt >= :minCachedAt
        LIMIT 1
    """
    )
    suspend fun getFresh(
        service: String,
        userId: String,
        postId: String,
        minCachedAt: Long
    ): PostContentCacheEntity?

    @Query("DELETE FROM post_content_cache WHERE cachedAt < :minCachedAt")
    suspend fun clearOlderThan(minCachedAt: Long)

    @Query("DELETE FROM post_content_cache")
    suspend fun clearAll()
}