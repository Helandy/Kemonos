package su.afk.kemonos.storage.repository.discord

import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType
import su.afk.kemonos.storage.api.repository.discord.IStoreDiscordRepository
import su.afk.kemonos.storage.entity.communityCache.dao.DiscordCacheDao
import javax.inject.Inject

internal class StoreDiscordRepository @Inject constructor(
    private val dao: DiscordCacheDao,
) : IStoreDiscordRepository {

    override suspend fun getFreshJsonOrNull(id: String, type: CommunityCacheType): String? {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        return dao.getFresh(id = id, type = type, minCachedAt = minTs)
    }

    override suspend fun putJson(id: String, type: CommunityCacheType, json: String) {
        dao.upsert(
            id = id,
            type = type,
            json = json,
            cachedAt = System.currentTimeMillis(),
        )
    }

    override suspend fun clearCacheOver7Days() {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        dao.deleteOlderThan(minTs)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}
