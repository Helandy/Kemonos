package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.posts.data.PostsRepository
import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain
import javax.inject.Inject

internal class GetHashLookupUseCase @Inject constructor(
    private val repository: PostsRepository,
) {
    suspend operator fun invoke(hash: String): HashLookupDomain = repository.searchHash(hash)
}
