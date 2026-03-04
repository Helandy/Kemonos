package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain
import su.afk.kemonos.posts.domain.repository.IPostsRepository
import javax.inject.Inject

internal class GetHashLookupUseCase @Inject constructor(
    private val repository: IPostsRepository,
) {
    suspend operator fun invoke(hash: String): HashLookupDomain = repository.searchHash(hash)
}
