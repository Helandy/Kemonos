package su.afk.kemonos.storage.useCases.profilePosts

import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.storage.api.profilePosts.IStorageCreatorPostsCacheUseCase
import su.afk.kemonos.storage.repository.profilePosts.IStorageCreatorPostsCacheRepository
import javax.inject.Inject

internal class StorageCreatorPostsCacheUseCase @Inject constructor(
    private val repo: IStorageCreatorPostsCacheRepository
) : IStorageCreatorPostsCacheUseCase {

    override suspend fun getFreshPageOrNull(queryKey: String, offset: Int): List<PostDomain>? =
        repo.getFreshPageOrNull(queryKey, offset)

    override suspend fun getStalePageOrEmpty(queryKey: String, offset: Int): List<PostDomain> =
        repo.getStalePageOrEmpty(queryKey, offset)

    override suspend fun putPage(queryKey: String, offset: Int, items: List<PostDomain>) =
        repo.putPage(queryKey, offset, items)

    override suspend fun clearPage(queryKey: String, offset: Int) =
        repo.clearPage(queryKey, offset)

    override suspend fun clearAll() = repo.clearAll()
}