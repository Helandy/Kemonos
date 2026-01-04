package su.afk.kemonos.storage.repository.popular

import kotlinx.serialization.json.Json
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_1_HOURS
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity
import su.afk.kemonos.storage.entity.popular.dao.CoomerPostsPopularCacheDao
import su.afk.kemonos.storage.entity.popular.dao.KemonoPostsPopularCacheDao
import javax.inject.Inject

interface IStoragePopularPostsCacheRepository {
    suspend fun getFreshOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts?
    suspend fun getStaleOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts?
    suspend fun put(site: SelectedSite, queryKey: String, offset: Int, value: PopularPosts)
    suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int)
    suspend fun clearCache(site: SelectedSite)
    suspend fun clearAll(site: SelectedSite)
}

internal class StoragePopularPostsCacheRepository @Inject constructor(
    private val kemonoDao: KemonoPostsPopularCacheDao,
    private val coomerDao: CoomerPostsPopularCacheDao,
    private val json: Json,
) : IStoragePopularPostsCacheRepository {

    override suspend fun getFreshOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts? {
        val row = when (site) {
            SelectedSite.K -> kemonoDao.get(queryKey, offset)
            SelectedSite.C -> coomerDao.get(queryKey, offset)
        } ?: return null

        if (!isFresh(row.updatedAt, row.queryKey)) return null
        return decode(row.payloadJson)
    }

    override suspend fun getStaleOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts? {
        val row = when (site) {
            SelectedSite.K -> kemonoDao.get(queryKey, offset)
            SelectedSite.C -> coomerDao.get(queryKey, offset)
        } ?: return null

        return decode(row.payloadJson)
    }

    override suspend fun put(site: SelectedSite, queryKey: String, offset: Int, value: PopularPosts) {
        val entity = PostsPopularCacheEntity(
            queryKey = queryKey,
            offset = offset,
            updatedAt = System.currentTimeMillis(),
            payloadJson = encode(value)
        )

        when (site) {
            SelectedSite.K -> kemonoDao.upsert(entity)
            SelectedSite.C -> coomerDao.upsert(entity)
        }
    }

    override suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int) {
        when (site) {
            SelectedSite.K -> kemonoDao.delete(queryKey, offset)
            SelectedSite.C -> coomerDao.delete(queryKey, offset)
        }
    }

    override suspend fun clearAll(site: SelectedSite) {
        when (site) {
            SelectedSite.K -> kemonoDao.clearAll()
            SelectedSite.C -> coomerDao.clearAll()
        }
    }

    override suspend fun clearCache(site: SelectedSite) {
        val now = System.currentTimeMillis()
        val shortMinTs = now - TTL_1_HOURS
        val longMinTs = now - TTL_3_DAYS

        when (site) {
            SelectedSite.K -> {
                kemonoDao.deleteExpiredByPeriods(shortMinTs, SHORT_PERIODS)
                kemonoDao.deleteExpiredByPeriods(longMinTs, LONG_PERIODS)
            }
            SelectedSite.C -> {
                coomerDao.deleteExpiredByPeriods(shortMinTs, SHORT_PERIODS)
                coomerDao.deleteExpiredByPeriods(longMinTs, LONG_PERIODS)
            }
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