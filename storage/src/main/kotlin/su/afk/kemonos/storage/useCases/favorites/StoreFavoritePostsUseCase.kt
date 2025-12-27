package su.afk.kemonos.storage.useCases.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import su.afk.kemonos.storage.repository.favorites.post.IStoreFavoritePostsRepository
import javax.inject.Inject

internal class StoreFavoritePostsUseCase @Inject constructor(
    private val repo: IStoreFavoritePostsRepository
) : IStoreFavoritePostsUseCase {
    override suspend fun getAll(site: SelectedSite) = repo.getAll(site = site)
    override suspend fun replaceAll(site: SelectedSite, items: List<PostDomain>) = repo.replaceAll(site = site, items)
    override suspend fun clear(site: SelectedSite) = repo.clear(site = site)
    override suspend fun isCacheFresh(site: SelectedSite) = repo.isCacheFresh(site = site)
    override suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean =
        repo.exists(site = site, service, creatorId, postId)

    override suspend fun add(site: SelectedSite, item: PostDomain) = repo.add(site = site, item)
    override suspend fun remove(site: SelectedSite, service: String, creatorId: String, postId: String) =
        repo.remove(site = site, service, creatorId, postId)
}