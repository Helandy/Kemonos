package su.afk.kemonos.storage.api.post

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain

interface IPostUseCase {
    suspend fun getFreshOrNull(service: String, userId: String, postId: String): PostContentDomain?
    suspend fun getOrNull(service: String, userId: String, postId: String): PostContentDomain?
    suspend fun upsert(item: PostContentDomain)
}