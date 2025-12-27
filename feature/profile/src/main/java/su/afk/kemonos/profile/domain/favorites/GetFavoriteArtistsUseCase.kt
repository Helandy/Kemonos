package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.profile.data.IFavoritesRepository
import javax.inject.Inject

internal class GetFavoriteArtistsUseCase @Inject constructor(
    private val repository: IFavoritesRepository
) {
    suspend operator fun invoke(site: SelectedSite): List<FavoriteArtist> {
        return repository.getFavoriteArtists(site = site)
    }
}