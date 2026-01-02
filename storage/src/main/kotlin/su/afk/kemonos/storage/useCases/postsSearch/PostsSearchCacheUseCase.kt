package su.afk.kemonos.storage.useCases.postsSearch

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.storage.api.postsSearch.IPostsSearchCacheUseCase
import su.afk.kemonos.storage.repository.postsSearch.IPostsSearchCacheRepository
import javax.inject.Inject

internal class PostsSearchCacheUseCase @Inject constructor(
    private val repo: IPostsSearchCacheRepository,
) : IPostsSearchCacheUseCase {

    override suspend fun getFreshPageOrNull(site: SelectedSite, queryKey: String, offset: Int): List<PostDomain>? =
        repo.getFreshPageOrNull(site = site, queryKey, offset)

    override suspend fun getStalePageOrEmpty(site: SelectedSite, queryKey: String, offset: Int): List<PostDomain> =
        repo.getStalePageOrEmpty(site = site, queryKey, offset)

    override suspend fun putPage(site: SelectedSite, queryKey: String, offset: Int, items: List<PostDomain>) =
        repo.putPage(site = site, queryKey, offset, items)

    override suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int) =
        repo.clearPage(site = site, queryKey, offset)

    override suspend fun clearCache(site: SelectedSite) =
        repo.clearCache(site = site)
}