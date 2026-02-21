package su.afk.kemonos.storage.repository.blacklist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.entity.blacklist.BlacklistedAuthorEntity
import su.afk.kemonos.storage.entity.blacklist.dao.BlacklistedAuthorsDao
import javax.inject.Inject

internal class StoreBlacklistedAuthorsRepository @Inject constructor(
    private val dao: BlacklistedAuthorsDao,
) : IStoreBlacklistedAuthorsRepository {
    override fun observeAll(): Flow<List<BlacklistedAuthor>> =
        dao.observeAll().map { items -> items.map { it.toDomain() } }

    override fun observeContains(service: String, creatorId: String): Flow<Boolean> =
        dao.observeContains(service = service, creatorId = creatorId)

    override suspend fun upsert(author: BlacklistedAuthor) {
        dao.upsert(BlacklistedAuthorEntity.fromDomain(author))
    }

    override suspend fun remove(service: String, creatorId: String) {
        dao.remove(service = service, creatorId = creatorId)
    }

    override suspend fun contains(service: String, creatorId: String): Boolean =
        dao.contains(service = service, creatorId = creatorId)
}
