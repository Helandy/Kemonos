package su.afk.kemonos.creatorPost.domain.useCase

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.data.repository.IPostRepository
import javax.inject.Inject

internal class GetPostUseCase @Inject constructor(
    private val repository: IPostRepository
) {
    suspend operator fun invoke(service: String, id: String, postId: String): PostContentDomain? {
        return repository.getPost(service, id, postId)
    }
}