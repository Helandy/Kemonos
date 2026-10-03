package su.afk.kemonos.creatorPost.domain.useCase

import su.afk.kemonos.domain.models.post.PostContentDomain
import su.afk.kemonos.creatorPost.domain.repository.IPostRepository
import javax.inject.Inject

internal class GetPostUseCase @Inject constructor(
    private val repository: IPostRepository
) {
    suspend operator fun invoke(service: String, id: String, postId: String): PostContentDomain? {
        return repository.getPost(service, id, postId)
    }

    suspend fun getRevision(
        service: String,
        id: String,
        postId: String,
        revisionId: Long,
    ): PostContentDomain? {
        return repository.getPostRevision(service, id, postId, revisionId)
    }
}
