package su.afk.kemonos.storage.entity.postsSearch.dao

import androidx.room.*
import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity

@Dao
interface KemonoPostsSearchCacheDao {

    // ----- READ -----

    @Query(
        """
        SELECT * FROM posts_search_cache
        WHERE queryKey = :queryKey AND offset = :offset
        ORDER BY indexInPage ASC
        """
    )
    suspend fun getPage(queryKey: String, offset: Int): List<PostsSearchCacheEntity>

    @Query(
        """
        SELECT * FROM posts_search_cache
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
    ): List<PostsSearchCacheEntity>

    // ----- WRITE -----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PostsSearchCacheEntity>)

    @Query("DELETE FROM posts_search_cache WHERE queryKey = :queryKey AND offset = :offset")
    suspend fun clearPage(queryKey: String, offset: Int)

    /**
     * Атомарно заменяем страницу:
     * 1) удаляем старую
     * 2) вставляем новую
     */
    @Transaction
    suspend fun replacePage(
        queryKey: String,
        offset: Int,
        items: List<PostsSearchCacheEntity>
    ) {
        clearPage(queryKey, offset)
        if (items.isNotEmpty()) upsertAll(items)
    }

    // ----- CLEANUP -----

    @Query("DELETE FROM posts_search_cache WHERE updatedAt < :minUpdatedAt")
    suspend fun deleteOlderThan(minUpdatedAt: Long)

    @Query("DELETE FROM posts_search_cache")
    suspend fun clearAll()
}