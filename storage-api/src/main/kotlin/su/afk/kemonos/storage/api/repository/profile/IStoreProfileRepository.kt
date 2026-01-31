package su.afk.kemonos.storage.api.repository.profile

import su.afk.kemonos.domain.models.Profile

interface IStoreProfileRepository {
    suspend fun getProfileFreshOrNull(service: String, id: String): Profile?
    suspend fun updateProfile(profile: Profile)
    suspend fun clear()
    suspend fun clearCacheOver24Hours()
}