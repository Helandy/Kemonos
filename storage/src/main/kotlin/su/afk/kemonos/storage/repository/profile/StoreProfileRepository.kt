package su.afk.kemonos.storage.repository.profile

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.storage.api.repository.profile.IStoreProfileRepository
import su.afk.kemonos.storage.entity.profile.ProfileEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.profile.ProfileEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.profile.dao.ProfileDao
import javax.inject.Inject

internal class StoreProfileRepository @Inject constructor(
    private val dao: ProfileDao,
) : IStoreProfileRepository {

    override suspend fun getProfileFreshOrNull(site: SelectedSite, service: String, id: String): Profile? {
        val minTs = System.currentTimeMillis() - TTL_3_DAYS

        return dao.getFreshProfile(
            id = id,
            service = service.cacheKey(site),
            minCachedAt = minTs
        )?.toDomain()?.copy(service = service)
    }

    override suspend fun updateProfile(site: SelectedSite, profile: Profile) {
        dao.insert(profile.toEntity().copy(service = profile.service.cacheKey(site)))
    }

    override suspend fun clear() {
        dao.clear()
    }

    override suspend fun clearCacheOver24Hours() {
        val minTs = System.currentTimeMillis() - TTL_3_DAYS
        dao.deleteOlderThan(minTs)
    }

    private fun String.cacheKey(site: SelectedSite): String = "${site.name}:$this"
}
