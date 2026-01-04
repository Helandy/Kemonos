package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.profile.data.FreshFavoriteArtistsUpdates
import su.afk.kemonos.profile.data.IFavoritesRepository
import javax.inject.Inject

internal class GetFavoriteArtistsUseCase @Inject constructor(
    private val repository: IFavoritesRepository,
    private val computeFreshUpdates: IComputeFreshFavoriteArtistsUpdatesUseCase,
) : IGetFavoriteArtistsUseCase {

    override suspend operator fun invoke(
        site: SelectedSite,
        checkDifferent: Boolean
    ): List<FavoriteArtist> {

        if (!checkDifferent) {
            return repository.getFavoriteArtists(site = site, getOldCache = false)
        }

        val oldCache = repository.getFavoriteArtists(
            site = site,
            getOldCache = true,
            forceRefresh = false,
        )

        val network = repository.getFavoriteArtists(
            site = site,
            getOldCache = false,
            forceRefresh = true,
        )

        val updatedKeys = computeFreshUpdates(
            site = site,
            oldCache = oldCache,
            network = network
        )

        FreshFavoriteArtistsUpdates.set(site, updatedKeys)

        return network
    }
}