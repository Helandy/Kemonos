package su.afk.kemonos.storage.repository.profile

import su.afk.kemonos.domain.domain.models.Profile
import su.afk.kemonos.storage.entity.profile.ProfileEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.profile.ProfileEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.profile.dao.ProfileDao
import javax.inject.Inject

interface IStoreProfileRepository {
    suspend fun getProfileFreshOrNull(service: String, id: String): Profile?
    suspend fun updateProfile(profile: Profile)
    suspend fun clear()
    suspend fun clearCacheOver24Hours()
}

internal class StoreProfileRepository @Inject constructor(
    private val dao: ProfileDao,
) : IStoreProfileRepository {

    override suspend fun getProfileFreshOrNull(service: String, id: String): Profile? {
        val minTs = System.currentTimeMillis() - CACHE_TTL_MS
        return dao.getFreshProfile(
            id = id,
            service = service,
            minCachedAt = minTs
        )?.toDomain()
    }

    override suspend fun updateProfile(profile: Profile) {
        dao.insert(profile.toEntity())
    }

    override suspend fun clear() {
        dao.clear()
    }

    override suspend fun clearCacheOver24Hours() {
        val minTs = System.currentTimeMillis() - CACHE_TTL_MS
        dao.deleteOlderThan(minTs)
    }

    private companion object {
        const val CACHE_TTL_MS = 24L * 60 * 60 * 1000 // 24 часа
    }
}