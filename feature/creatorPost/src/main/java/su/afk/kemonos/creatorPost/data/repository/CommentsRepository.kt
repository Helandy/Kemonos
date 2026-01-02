package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain.Companion.emptyComment
import su.afk.kemonos.creatorPost.data.api.PostsApi
import su.afk.kemonos.creatorPost.data.dto.comments.ProfilePostCommentsDto.Companion.toDomain
import su.afk.kemonos.network.util.call
import su.afk.kemonos.storage.api.IStoreCommentsUseCase
import javax.inject.Inject

internal interface ICommentsRepository {
    suspend fun getComments(service: String, id: String, postId: String): List<CommentDomain>
}

internal class CommentsRepository @Inject constructor(
    private val api: PostsApi,
    private val storeCommentsUseCase: IStoreCommentsUseCase,
) : ICommentsRepository {

    /** Получение комментариев к посту */
    override suspend fun getComments(service: String, id: String, postId: String): List<CommentDomain> {
        storeCommentsUseCase.getComments(service, id, postId)?.let { return it }

        val commentsFromNet = api.getProfilePostComments(service, id, postId).call { dto ->
            dto.map { it.toDomain() }
        }

        if (commentsFromNet.isEmpty()) {
            storeCommentsUseCase.updateComments(service, id, postId, listOf(emptyComment()))
        } else {
            storeCommentsUseCase.updateComments(service, id, postId, commentsFromNet)
        }

        return commentsFromNet
    }
}