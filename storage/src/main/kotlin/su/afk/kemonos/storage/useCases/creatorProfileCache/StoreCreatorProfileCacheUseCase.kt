package su.afk.kemonos.storage.useCases.creatorProfileCache

import su.afk.kemonos.storage.api.creatorProfileCache.CreatorProfileCacheType
import su.afk.kemonos.storage.api.creatorProfileCache.IStoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.repository.creatorProfileCache.IStoreCreatorProfileCacheRepository
import javax.inject.Inject

internal class StoreCreatorProfileCacheUseCase @Inject constructor(
    private val repository: IStoreCreatorProfileCacheRepository
) : IStoreCreatorProfileCacheUseCase {

    override suspend fun getFreshJsonOrNull(
        service: String,
        id: String,
        type: CreatorProfileCacheType
    ): String? =
        repository.getFreshJsonOrNull(service, id, type)

    override suspend fun getJsonOrNull(
        service: String,
        id: String,
        type: CreatorProfileCacheType
    ): String? =
        repository.getJsonOrNull(service, id, type)

    override suspend fun putJson(
        service: String,
        id: String,
        type: CreatorProfileCacheType,
        json: String
    ) {
        repository.putJson(service, id, type, json)
    }

    override suspend fun clearProfile(service: String, id: String) {
        repository.clearProfile(service, id)
    }

    override suspend fun clearAll() {
        repository.clearAll()
    }
}