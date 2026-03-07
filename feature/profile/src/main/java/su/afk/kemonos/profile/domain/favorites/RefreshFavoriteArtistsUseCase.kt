package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.profile.api.domain.IRefreshFavoriteArtistsUseCase
import su.afk.kemonos.profile.domain.repository.IFavoritesRepository
import javax.inject.Inject

internal class RefreshFavoriteArtistsUseCase @Inject constructor(
    private val repository: IFavoritesRepository
) : IRefreshFavoriteArtistsUseCase {

    override suspend fun refreshFavoriteArtists(site: SelectedSite): List<FavoriteArtist> =
        repository.refreshFavoriteArtists(site = site)
}