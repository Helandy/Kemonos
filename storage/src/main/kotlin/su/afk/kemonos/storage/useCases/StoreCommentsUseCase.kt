package su.afk.kemonos.storage.useCases

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.storage.api.IStoreCommentsUseCase
import su.afk.kemonos.storage.repository.comments.IStoreCommentsRepository
import javax.inject.Inject

internal class StoreCommentsUseCase @Inject constructor(
    private val repo: IStoreCommentsRepository
) : IStoreCommentsUseCase {

    /**
     * Вернёт список, если кэш "свежий" (< 1 суток),
     * иначе null
     */
    override suspend fun getComments(
        service: String,
        id: String,
        postId: String
    ): List<CommentDomain>? {
        return repo.getCommentsFreshOrNull(service, id, postId)
    }

    override suspend fun updateComments(
        service: String,
        id: String,
        postId: String,
        comments: List<CommentDomain>
    ) {
        repo.updateComments(service, id, postId, comments)
    }

    override suspend fun clearCacheOver7Days() {
        repo.clearCacheOver7Days()
    }
}