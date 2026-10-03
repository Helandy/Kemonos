package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.domain.models.post.CommentDomain

internal interface ICommentsRepository {
    suspend fun getComments(service: String, id: String, postId: String): List<CommentDomain>
}
