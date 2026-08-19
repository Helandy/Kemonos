package su.afk.kemonos.storage.entity.popular.dao

import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity

/**
 * Общий контракт DAO. У каждого источника своя база, но набор запросов одинаковый —
 * это позволяет выбирать DAO по источнику через map, а не через when.
 */
interface PostsPopularCacheDao {

    suspend fun get(queryKey: String, offset: Int): PostsPopularCacheEntity?

    suspend fun upsert(entity: PostsPopularCacheEntity)

    suspend fun delete(queryKey: String, offset: Int)

    suspend fun clearAll()

    suspend fun deleteExpiredByPeriods(minTs: Long, periods: List<String>)

    suspend fun deleteExpiredAll(minTs: Long)
}
