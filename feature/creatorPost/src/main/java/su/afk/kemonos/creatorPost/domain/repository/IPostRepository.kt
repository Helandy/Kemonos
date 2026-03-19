package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain

internal interface IPostRepository {
    suspend fun getPost(service: String, id: String, postId: String): PostContentDomain?
    suspend fun getPostRevision(
        service: String,
        id: String,
        postId: String,
        revisionId: Long,
    ): PostContentDomain?
}
