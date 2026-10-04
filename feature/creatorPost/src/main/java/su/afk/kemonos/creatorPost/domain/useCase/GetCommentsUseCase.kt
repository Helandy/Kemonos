package su.afk.kemonos.creatorPost.domain.useCase

import su.afk.kemonos.domain.models.post.CommentDomain
import su.afk.kemonos.creatorPost.domain.repository.ICommentsRepository
import javax.inject.Inject

internal class GetCommentsUseCase @Inject constructor(
    private val repository: ICommentsRepository
) {
    suspend operator fun invoke(service: String, id: String, postId: String): List<CommentDomain> {
        return repository.getComments(service, id, postId)
    }
}
