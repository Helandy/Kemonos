package su.afk.kemonos.storage.entity.postsSearch.dao

import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity

/**
 * Общий контракт DAO. У каждого источника своя база, но набор запросов одинаковый —
 * это позволяет выбирать DAO по источнику через map, а не через when.
 */
interface PostsSearchCacheDao {

    suspend fun getPage(queryKey: String, offset: Int): List<PostsSearchCacheEntity>

    suspend fun getFreshPage(queryKey: String, offset: Int, minUpdatedAt: Long): List<PostsSearchCacheEntity>

    suspend fun upsertAll(items: List<PostsSearchCacheEntity>)

    suspend fun clearPage(queryKey: String, offset: Int)

    suspend fun replacePage(queryKey: String, offset: Int, items: List<PostsSearchCacheEntity>)

    suspend fun deleteOlderThan(minUpdatedAt: Long)

    suspend fun clearAll()
}
