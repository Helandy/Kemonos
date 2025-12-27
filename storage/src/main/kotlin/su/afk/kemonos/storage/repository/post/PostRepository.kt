package su.afk.kemonos.storage.repository.post

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.storage.entity.post.dao.PostContentCacheDao
import su.afk.kemonos.storage.entity.post.mapper.PostContentCacheMapper
import javax.inject.Inject

interface IPostStorageRepository {
    suspend fun getOrNull(service: String, userId: String, postId: String): PostContentDomain?
    suspend fun getFreshOrNull(service: String, userId: String, postId: String): PostContentDomain?
    suspend fun upsert(item: PostContentDomain)
    suspend fun clearCache()
    suspend fun clearAll()
}

internal class PostStorageRepository @Inject constructor(
    private val dao: PostContentCacheDao,
    private val mapper: PostContentCacheMapper
) : IPostStorageRepository {

    override suspend fun getOrNull(service: String, userId: String, postId: String): PostContentDomain? =
        dao.get(service, userId, postId)?.let(mapper::toDomain)

    override suspend fun getFreshOrNull(
        service: String,
        userId: String,
        postId: String,
    ): PostContentDomain? {
        val minTs = System.currentTimeMillis() - CACHE_TIME
        return dao.getFresh(service, userId, postId, minTs)?.let(mapper::toDomain)
    }

    override suspend fun upsert(item: PostContentDomain) {
        dao.upsert(mapper.toEntity(item))
    }

    override suspend fun clearCache() {
        val minTs = System.currentTimeMillis() - CACHE_TIME
        dao.clearOlderThan(minTs)
    }

    override suspend fun clearAll() = dao.clearAll()

    private companion object {
        private const val CACHE_TIME = 7L * 24 * 60 * 60 * 1000 // 7 дней
    }
}