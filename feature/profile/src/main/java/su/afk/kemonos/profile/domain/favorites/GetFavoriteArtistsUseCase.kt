package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import su.afk.kemonos.profile.data.FreshFavoriteArtistsUpdates
import su.afk.kemonos.profile.data.IFavoritesRepository
import javax.inject.Inject

internal class GetFavoriteArtistsUseCase @Inject constructor(
    private val repository: IFavoritesRepository,
    private val computeFreshUpdates: IComputeFreshFavoriteArtistsUpdatesUseCase,
) : IGetFavoriteArtistsUseCase {

    override suspend operator fun invoke(
        site: SelectedSite,
        checkDifferent: Boolean,
        refresh: Boolean,
    ): List<FavoriteArtist> {

        // refresh
        if (refresh) {
            return repository.getFavoriteArtists(
                site = site,
                getOldCache = false,
                forceRefresh = true,
            )
        }

        // обычный сценарий без сравнения
        if (!checkDifferent) {
            return repository.getFavoriteArtists(
                site = site,
                getOldCache = false,
                forceRefresh = false,
            )
        }

        // сценарий с вычислением diff’ов
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