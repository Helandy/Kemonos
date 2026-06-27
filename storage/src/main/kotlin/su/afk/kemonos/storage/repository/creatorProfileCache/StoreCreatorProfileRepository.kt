package su.afk.kemonos.storage.repository.creatorProfileCache

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.storage.api.repository.creatorProfile.CreatorProfileCacheType
import su.afk.kemonos.storage.api.repository.creatorProfile.IStoreCreatorProfileRepository
import su.afk.kemonos.storage.entity.creatorProfileCache.dao.CreatorProfileCacheDao
import javax.inject.Inject

internal class StoreCreatorProfileRepository @Inject constructor(
    private val dao: CreatorProfileCacheDao,
) : IStoreCreatorProfileRepository {

    override suspend fun getFreshJsonOrNull(
        site: SelectedSite,
        service: String,
        id: String,
        type: CreatorProfileCacheType,
    ): String? {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        return dao.getFresh(service = service.cacheKey(site), profileId = id, type = type, minCachedAt = minTs)
    }

    override suspend fun getJsonOrNull(
        site: SelectedSite,
        service: String,
        id: String,
        type: CreatorProfileCacheType,
    ): String? =
        dao.get(service = service.cacheKey(site), profileId = id, type = type)

    override suspend fun putJson(
        site: SelectedSite,
        service: String,
        id: String,
        type: CreatorProfileCacheType,
        json: String,
    ) {
        dao.upsert(
            service = service.cacheKey(site),
            profileId = id,
            type = type,
            json = json,
            cachedAt = System.currentTimeMillis(),
        )
    }

    override suspend fun clearProfile(site: SelectedSite, service: String, id: String) {
        dao.clearProfile(service.cacheKey(site), profileId = id)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }

    override suspend fun clearCacheOver7Days() {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        dao.deleteOlderThan(minTs)
    }

    private fun String.cacheKey(site: SelectedSite): String = "${site.name}:$this"
}
