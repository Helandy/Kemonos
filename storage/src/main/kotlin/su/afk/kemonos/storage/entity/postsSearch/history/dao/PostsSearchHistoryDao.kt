package su.afk.kemonos.storage.entity.postsSearch.history.dao

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity

/**
 * Общий контракт DAO. У каждого источника своя база, но набор запросов одинаковый —
 * это позволяет выбирать DAO по источнику через map, а не через when.
 */
interface PostsSearchHistoryDao {

    fun observeRecent(limit: Int): Flow<List<String>>

    suspend fun upsert(item: PostsSearchHistoryEntity)

    suspend fun delete(query: String)

    suspend fun trimToLimit(limit: Int)

    suspend fun saveAndTrim(item: PostsSearchHistoryEntity, limit: Int)
}
