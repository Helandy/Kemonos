package su.afk.kemonos.storage.api.repository.comments

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain

interface IStoreCommentsRepository {
    suspend fun getCommentsFreshOrNull(service: String, userId: String, postId: String): List<CommentDomain>?
    suspend fun updateComments(service: String, userId: String, postId: String, comments: List<CommentDomain>)
    suspend fun clearCacheOver7Days()
}