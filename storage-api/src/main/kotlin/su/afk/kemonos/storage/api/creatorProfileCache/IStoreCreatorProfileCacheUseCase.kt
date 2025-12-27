package su.afk.kemonos.storage.api.creatorProfileCache

interface IStoreCreatorProfileCacheUseCase {

    suspend fun getFreshJsonOrNull(
        service: String,
        id: String,
        type: CreatorProfileCacheType
    ): String?

    suspend fun getJsonOrNull(
        service: String,
        id: String,
        type: CreatorProfileCacheType
    ): String?

    suspend fun putJson(
        service: String,
        id: String,
        type: CreatorProfileCacheType,
        json: String
    )

    suspend fun clearProfile(service: String, id: String)

    suspend fun clearAll()
}