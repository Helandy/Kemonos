package su.afk.kemonos.storage.api.repository.profile

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.Profile

interface IStoreProfileRepository {
    suspend fun getProfileFreshOrNull(site: SelectedSite, service: String, id: String): Profile?
    suspend fun updateProfile(site: SelectedSite, profile: Profile)
    suspend fun clear()
    suspend fun clearCacheOver24Hours()
}
