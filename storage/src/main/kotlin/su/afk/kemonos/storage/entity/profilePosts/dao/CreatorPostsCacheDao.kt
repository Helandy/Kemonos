package su.afk.kemonos.storage.entity.profilePosts.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.afk.kemonos.storage.entity.profilePosts.CreatorPostCacheEntity


@Dao
interface CreatorPostsCacheDao {

    // ---------- READ ----------

    @Query(
        """
        SELECT * FROM creator_posts_cache
        WHERE queryKey = :queryKey
          AND offset = :offset
          AND updatedAt >= :minUpdatedAt
        ORDER BY indexInPage ASC
        """
    )
    suspend fun getFreshPage(
        queryKey: String,
        offset: Int,
        minUpdatedAt: Long
    ): List<CreatorPostCacheEntity>

    @Query(
        """
        SELECT * FROM creator_posts_cache
        WHERE queryKey = :queryKey
          AND offset = :offset
        ORDER BY indexInPage ASC
        """
    )
    suspend fun getPage(queryKey: String, offset: Int): List<CreatorPostCacheEntity>

    // ---------- WRITE ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CreatorPostCacheEntity>)

    @Query("DELETE FROM creator_posts_cache WHERE queryKey = :queryKey AND offset = :offset")
    suspend fun deletePage(queryKey: String, offset: Int)

    // ---------- CLEANUP ----------

    /**
     * Удаляем протухшие страницы типа search/tag.
     * Логика: queryKey НЕ заканчивается на "||" => есть search или tag.
     */
    @Query(
        """
        DELETE FROM creator_posts_cache
        WHERE updatedAt < :minUpdatedAt
          AND queryKey NOT LIKE :defaultSuffix
        """
    )
    suspend fun deleteExpiredSearchTag(
        minUpdatedAt: Long,
        defaultSuffix: String = "%||"
    )

    /**
     * Удаляем протухшие default-страницы (без search/tag).
     * Логика: queryKey заканчивается на "||".
     */
    @Query(
        """
        DELETE FROM creator_posts_cache
        WHERE updatedAt < :minUpdatedAt
          AND queryKey LIKE :defaultSuffix
        """
    )
    suspend fun deleteExpiredDefault(
        minUpdatedAt: Long,
        defaultSuffix: String = "%||"
    )

    @Query("DELETE FROM creator_posts_cache")
    suspend fun clearAll()
}