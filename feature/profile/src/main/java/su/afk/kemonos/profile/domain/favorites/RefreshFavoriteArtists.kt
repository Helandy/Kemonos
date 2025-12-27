package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.IRefreshFavoriteArtists
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.profile.data.IFavoritesRepository
import javax.inject.Inject

internal class RefreshFavoriteArtists @Inject constructor(
    private val repository: IFavoritesRepository
) : IRefreshFavoriteArtists {

    override suspend fun refreshFavoriteArtists(site: SelectedSite): List<FavoriteArtist> =
        repository.refreshFavoriteArtists(site = site)
}