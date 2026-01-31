package su.afk.kemonos.storage.api.repository.post

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain

interface IStoragePostStorageRepository {
    suspend fun getOrNull(service: String, userId: String, postId: String): PostContentDomain?
    suspend fun getFreshOrNull(service: String, userId: String, postId: String): PostContentDomain?
    suspend fun upsert(item: PostContentDomain)
    suspend fun clearCache()
    suspend fun clearAll()
}