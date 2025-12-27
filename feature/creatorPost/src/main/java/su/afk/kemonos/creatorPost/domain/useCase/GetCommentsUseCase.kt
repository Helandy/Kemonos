package su.afk.kemonos.creatorPost.domain.useCase

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.data.repository.ICommentsRepository
import javax.inject.Inject

internal class GetCommentsUseCase @Inject constructor(
    private val repository: ICommentsRepository
) {
    suspend operator fun invoke(service: String, id: String, postId: String): List<CommentDomain> {
        return repository.getComments(service, id, postId)
    }
}