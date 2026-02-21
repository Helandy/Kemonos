package su.afk.kemonos.storage.api.repository.blacklist

import kotlinx.coroutines.flow.Flow

interface IStoreBlacklistedAuthorsRepository {
    fun observeAll(): Flow<List<BlacklistedAuthor>>

    fun observeContains(service: String, creatorId: String): Flow<Boolean>

    suspend fun upsert(author: BlacklistedAuthor)

    suspend fun remove(service: String, creatorId: String)

    suspend fun contains(service: String, creatorId: String): Boolean
}
