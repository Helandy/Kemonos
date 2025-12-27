package su.afk.kemonos.storage.useCases.post

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.storage.api.post.IPostUseCase
import su.afk.kemonos.storage.repository.post.IPostStorageRepository
import javax.inject.Inject

internal class PostUseCase @Inject constructor(
    private val repo: IPostStorageRepository
) : IPostUseCase {

    override suspend fun getFreshOrNull(service: String, userId: String, postId: String): PostContentDomain? =
        repo.getFreshOrNull(service, userId, postId)

    override suspend fun getOrNull(service: String, userId: String, postId: String): PostContentDomain? =
        repo.getOrNull(service, userId, postId)

    override suspend fun upsert(item: PostContentDomain) =
        repo.upsert(item)
}