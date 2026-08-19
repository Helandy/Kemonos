package su.afk.kemonos.storage.entity.postsSearch.history.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity

@Dao
interface KemonoPostsSearchHistoryDao : PostsSearchHistoryDao {
    @Query(
        """
        SELECT query FROM posts_search_history
        ORDER BY updatedAt DESC
        LIMIT :limit
        """
    )
    override fun observeRecent(limit: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsert(item: PostsSearchHistoryEntity)

    @Query("DELETE FROM posts_search_history WHERE query = :query")
    override suspend fun delete(query: String)

    @Query(
        """
        DELETE FROM posts_search_history
        WHERE query NOT IN (
            SELECT query
            FROM posts_search_history
            ORDER BY updatedAt DESC
            LIMIT :limit
        )
        """
    )
    override suspend fun trimToLimit(limit: Int)

    @Transaction
    override suspend fun saveAndTrim(item: PostsSearchHistoryEntity, limit: Int) {
        upsert(item)
        trimToLimit(limit)
    }
}
