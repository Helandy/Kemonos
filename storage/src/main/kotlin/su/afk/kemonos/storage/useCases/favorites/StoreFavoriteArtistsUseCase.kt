package su.afk.kemonos.storage.useCases.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.repository.favorites.artist.IStoreFavoriteArtistsRepository
import javax.inject.Inject

internal class StoreFavoriteArtistsUseCase @Inject constructor(
    private val repo: IStoreFavoriteArtistsRepository
) : IStoreFavoriteArtistsUseCase {
    override suspend fun getAll(site: SelectedSite) = repo.getAll(site = site)
    override suspend fun replaceAll(site: SelectedSite, items: List<FavoriteArtist>) =
        repo.replaceAll(site = site, items)

    override suspend fun remove(site: SelectedSite, service: String, id: String) = repo.remove(site = site, service, id)
    override suspend fun clear(site: SelectedSite) = repo.clear(site = site)
    override suspend fun isCacheFresh(site: SelectedSite) = repo.isCacheFresh(site = site)
    override suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean =
        repo.exists(site = site, service, creatorId)
}