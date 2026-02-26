package su.afk.kemonos.storage.repository.community

import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType
import su.afk.kemonos.storage.api.repository.community.IStoreCommunityRepository
import su.afk.kemonos.storage.entity.communityCache.CommunityCacheEntity
import su.afk.kemonos.storage.entity.communityCache.dao.CommunityCacheDao
import javax.inject.Inject

internal class StoreCommunityRepository @Inject constructor(
    private val dao: CommunityCacheDao,
) : IStoreCommunityRepository {

    override suspend fun getFreshJsonOrNull(service: String, id: String, type: CommunityCacheType): String? {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        return dao.getFresh(service = service, id = id, type = type, minCachedAt = minTs)?.json
    }

    override suspend fun putJson(service: String, id: String, type: CommunityCacheType, json: String) {
        dao.upsert(
            CommunityCacheEntity(
                service = service,
                id = id,
                type = type,
                json = json,
                cachedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}
