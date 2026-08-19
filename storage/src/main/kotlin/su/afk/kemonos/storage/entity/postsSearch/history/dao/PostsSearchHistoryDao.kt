package su.afk.kemonos.storage.entity.postsSearch.history.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity

/**
 * Общий контракт DAO вместе с запросами.
 *
 * У каждого источника своя база, но таблица и запросы одинаковые,
 * поэтому конкретные DAO — пустые наследники: Room генерирует реализацию под свою БД.
 */
@Dao
interface PostsSearchHistoryDao {
    @Query(
        """
        SELECT query FROM posts_search_history
        ORDER BY updatedAt DESC
        LIMIT :limit
        """
    )
    fun observeRecent(limit: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PostsSearchHistoryEntity)

    @Query("DELETE FROM posts_search_history WHERE query = :query")
    suspend fun delete(query: String)

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
    suspend fun trimToLimit(limit: Int)

    @Transaction
    suspend fun saveAndTrim(item: PostsSearchHistoryEntity, limit: Int) {
        upsert(item)
        trimToLimit(limit)
    }
}
