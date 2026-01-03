package su.afk.kemonos.storage.repository.creatorProfileCache

import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.storage.api.creatorProfileCache.CreatorProfileCacheType
import su.afk.kemonos.storage.entity.creatorProfileCache.CreatorProfileCacheEntity
import su.afk.kemonos.storage.entity.creatorProfileCache.dao.CreatorProfileCacheDao
import javax.inject.Inject

interface IStoreCreatorProfileCacheRepository {
    suspend fun getFreshJsonOrNull(service: String, id: String, type: CreatorProfileCacheType): String?
    suspend fun getJsonOrNull(service: String, id: String, type: CreatorProfileCacheType): String?
    suspend fun putJson(service: String, id: String, type: CreatorProfileCacheType, json: String)
    suspend fun clearProfile(service: String, id: String)
    suspend fun clearAll()
    suspend fun clearCacheOver7Days()
}

internal class StoreCreatorProfileCacheRepository @Inject constructor(
    private val dao: CreatorProfileCacheDao,
) : IStoreCreatorProfileCacheRepository {

    override suspend fun getFreshJsonOrNull(service: String, id: String, type: CreatorProfileCacheType): String? {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        return dao.getFresh(service = service, profileId = id, type = type, minCachedAt = minTs)?.json
    }

    override suspend fun getJsonOrNull(service: String, id: String, type: CreatorProfileCacheType): String? =
        dao.get(service = service, profileId = id, type = type)?.json

    override suspend fun putJson(service: String, id: String, type: CreatorProfileCacheType, json: String) {
        dao.upsert(
            CreatorProfileCacheEntity(
                service = service,
                profileId = id,
                type = type,
                json = json,
                cachedAt = System.currentTimeMillis(),
            )
        )
    }

    override suspend fun clearProfile(service: String, id: String) {
        dao.clearProfile(service, profileId = id)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }

    override suspend fun clearCacheOver7Days() {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        dao.deleteOlderThan(minTs)
    }
}