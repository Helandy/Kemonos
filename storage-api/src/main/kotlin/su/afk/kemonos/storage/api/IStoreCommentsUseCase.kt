package su.afk.kemonos.storage.api

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain


interface IStoreCommentsUseCase {
    suspend fun getComments(service: String, id: String, postId: String): List<CommentDomain>?
    suspend fun updateComments(service: String, id: String, postId: String, comments: List<CommentDomain>)
    suspend fun clearCacheOver7Days()
}