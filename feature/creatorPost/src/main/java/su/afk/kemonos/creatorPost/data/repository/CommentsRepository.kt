package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain.Companion.toCachePayload
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain.Companion.toExternalComments
import su.afk.kemonos.creatorPost.data.api.PostsApi
import su.afk.kemonos.creatorPost.data.dto.comments.ProfilePostCommentsDto.Companion.toDomain
import su.afk.kemonos.creatorPost.data.repository.helper.cacheFirstOrNetwork
import su.afk.kemonos.creatorPost.domain.repository.ICommentsRepository
import su.afk.kemonos.network.util.call
import su.afk.kemonos.storage.api.repository.comments.IStoreCommentsRepository
import javax.inject.Inject

internal class CommentsRepository @Inject constructor(
    private val api: PostsApi,
    private val store: IStoreCommentsRepository,
) : ICommentsRepository {

    /** Получение комментариев к посту */
    override suspend fun getComments(service: String, id: String, postId: String): List<CommentDomain> {
        return cacheFirstOrNetwork(
            freshCache = {
                store.getCommentsFreshOrNull(service, id, postId)
                    ?.toExternalComments()
            },
            network = {
                api.getProfilePostComments(service, id, postId).call { dto ->
                    dto.map { it.toDomain() }
                }
            },
            saveToCache = { fromNet ->
                store.updateComments(
                    service = service,
                    userId = id,
                    postId = postId,
                    comments = fromNet.toCachePayload(),
                )
            },
            staleCache = {
                store.getCommentsOrNull(service, id, postId)?.toExternalComments()
            }
        )
    }
}
