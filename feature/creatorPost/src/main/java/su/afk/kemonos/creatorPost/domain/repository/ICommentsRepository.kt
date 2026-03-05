package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain

internal interface ICommentsRepository {
    suspend fun getComments(service: String, id: String, postId: String): List<CommentDomain>
}
