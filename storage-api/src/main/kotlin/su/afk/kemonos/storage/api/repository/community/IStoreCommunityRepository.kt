package su.afk.kemonos.storage.api.repository.community

interface IStoreCommunityRepository {
    suspend fun getFreshJsonOrNull(service: String, id: String, type: CommunityCacheType): String?
    suspend fun putJson(service: String, id: String, type: CommunityCacheType, json: String)
    suspend fun clearAll()
}
