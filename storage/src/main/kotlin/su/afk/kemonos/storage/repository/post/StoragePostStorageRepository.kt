package su.afk.kemonos.storage.repository.post

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.storage.api.repository.post.IStoragePostStorageRepository
import su.afk.kemonos.storage.entity.post.dao.PostContentCacheDao
import su.afk.kemonos.storage.entity.post.mapper.PostContentCacheMapper
import javax.inject.Inject

internal class StoragePostStorageRepository @Inject constructor(
    private val dao: PostContentCacheDao,
    private val mapper: PostContentCacheMapper
) : IStoragePostStorageRepository {

    override suspend fun getOrNull(service: String, userId: String, postId: String): PostContentDomain? =
        dao.get(service, userId, postId)?.let(mapper::toDomain)

    override suspend fun getFreshOrNull(
        service: String,
        userId: String,
        postId: String,
    ): PostContentDomain? {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        return dao.getFresh(service, userId, postId, minTs)?.let(mapper::toDomain)
    }

    override suspend fun upsert(item: PostContentDomain) {
        dao.upsert(mapper.toEntity(item))
    }

    override suspend fun clearCache() {
        val minTs = System.currentTimeMillis() - TTL_7_DAYS
        dao.clearOlderThan(minTs)
    }

    override suspend fun clearAll() = dao.clearAll()
}