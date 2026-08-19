package su.afk.kemonos.storage.repository.popular

import su.afk.kemonos.storage.entity.popular.dao.PostsPopularCacheDao
import kotlinx.serialization.json.Json
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_1_HOURS
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.storage.api.repository.popular.IStoragePopularPostsRepository
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity
import javax.inject.Inject

internal class StoragePopularPostsRepository @Inject constructor(
    private val daos: Map<SelectedSite, @JvmSuppressWildcards PostsPopularCacheDao>,
    private val json: Json,
) : IStoragePopularPostsRepository {

    private fun dao(site: SelectedSite): PostsPopularCacheDao = daos.getValue(site)

    override suspend fun getFreshOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts? {
        val row = dao(site).get(queryKey, offset) ?: return null

        if (!isFresh(row.updatedAt, row.queryKey)) return null
        return decode(row.payloadJson)
    }

    override suspend fun getStaleOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts? {
        val row = dao(site).get(queryKey, offset) ?: return null

        return decode(row.payloadJson)
    }

    override suspend fun put(site: SelectedSite, queryKey: String, offset: Int, value: PopularPosts) {
        val entity = PostsPopularCacheEntity(
            queryKey = queryKey,
            offset = offset,
            updatedAt = System.currentTimeMillis(),
            payloadJson = encode(value)
        )

        dao(site).upsert(entity)
    }

    override suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int) {
        dao(site).delete(queryKey, offset)
    }

    override suspend fun clearAll(site: SelectedSite) {
        dao(site).clearAll()
    }

    override suspend fun clearCache(site: SelectedSite) {
        val now = System.currentTimeMillis()
        val shortMinTs = now - TTL_1_HOURS
        val longMinTs = now - TTL_3_DAYS

        dao(site).apply {
            deleteExpiredByPeriods(shortMinTs, SHORT_PERIODS)
            deleteExpiredByPeriods(longMinTs, LONG_PERIODS)
        }
    }

    private fun encode(value: PopularPosts): String =
        json.encodeToString(PopularPosts.serializer(), value)

    private fun decode(payloadJson: String): PopularPosts =
        json.decodeFromString(PopularPosts.serializer(), payloadJson)

    private fun isFresh(updatedAt: Long, queryKey: String): Boolean =
        System.currentTimeMillis() - updatedAt < ttlFor(queryKey)

    private fun ttlFor(queryKey: String): Long {
        val period = queryKey.substringBefore('|', missingDelimiterValue = queryKey)
        return if (period == "RECENT" || period == "DAY") TTL_1_HOURS else TTL_3_DAYS
    }

    private companion object {
        private val SHORT_PERIODS = listOf("RECENT", "DAY")
        private val LONG_PERIODS = listOf("WEEK", "MONTH")
    }
}
