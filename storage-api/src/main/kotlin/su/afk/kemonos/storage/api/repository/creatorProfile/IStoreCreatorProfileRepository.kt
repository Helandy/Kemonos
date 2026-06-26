package su.afk.kemonos.storage.api.repository.creatorProfile

import su.afk.kemonos.domain.SelectedSite

interface IStoreCreatorProfileRepository {
    suspend fun getFreshJsonOrNull(site: SelectedSite, service: String, id: String, type: CreatorProfileCacheType): String?
    suspend fun getJsonOrNull(site: SelectedSite, service: String, id: String, type: CreatorProfileCacheType): String?
    suspend fun putJson(site: SelectedSite, service: String, id: String, type: CreatorProfileCacheType, json: String)
    suspend fun clearProfile(site: SelectedSite, service: String, id: String)
    suspend fun clearAll()
    suspend fun clearCacheOver7Days()
}
