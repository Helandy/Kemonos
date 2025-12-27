package su.afk.kemonos.storage.useCases.popular

import su.afk.kemonos.api.domain.popular.PopularPosts
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.popular.IPopularPostsCacheUseCase
import su.afk.kemonos.storage.repository.popular.IPopularPostsCacheRepository
import javax.inject.Inject

internal class PopularPostsCacheUseCase @Inject constructor(
    private val repo: IPopularPostsCacheRepository,
) : IPopularPostsCacheUseCase {

    override suspend fun getFreshOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts? =
        repo.getFreshOrNull(site = site, queryKey, offset)

    override suspend fun getStaleOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts? =
        repo.getStaleOrNull(site = site, queryKey, offset)

    override suspend fun put(site: SelectedSite, queryKey: String, offset: Int, value: PopularPosts) =
        repo.put(site = site, queryKey, offset, value)

    override suspend fun clearCache(site: SelectedSite) = repo.clearCache(site = site)
}