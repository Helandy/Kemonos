package su.afk.kemonos.profile.api.domain

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.FavoriteArtist

interface IRefreshFavoriteArtists {
    suspend fun refreshFavoriteArtists(site: SelectedSite): List<FavoriteArtist>
}