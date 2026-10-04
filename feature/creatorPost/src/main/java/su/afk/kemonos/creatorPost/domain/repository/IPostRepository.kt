package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.domain.models.post.PostContentDomain

internal interface IPostRepository {
    suspend fun getPost(service: String, id: String, postId: String): PostContentDomain?
    suspend fun getPostRevision(
        service: String,
        id: String,
        postId: String,
        revisionId: Long,
    ): PostContentDomain?
}
