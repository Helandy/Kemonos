package su.afk.kemonos.storage.useCases.post

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.storage.api.post.IStoragePostUseCase
import su.afk.kemonos.storage.repository.post.IStoragePostStorageRepository
import javax.inject.Inject

internal class StoragePostUseCase @Inject constructor(
    private val repo: IStoragePostStorageRepository
) : IStoragePostUseCase {

    override suspend fun getFreshOrNull(service: String, userId: String, postId: String): PostContentDomain? =
        repo.getFreshOrNull(service, userId, postId)

    override suspend fun getOrNull(service: String, userId: String, postId: String): PostContentDomain? =
        repo.getOrNull(service, userId, postId)

    override suspend fun upsert(item: PostContentDomain) =
        repo.upsert(item)

    override suspend fun clearAll() = repo.clearAll()
}