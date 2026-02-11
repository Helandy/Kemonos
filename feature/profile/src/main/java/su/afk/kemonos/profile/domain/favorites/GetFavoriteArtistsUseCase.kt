package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FreshFavoriteArtistKey
import su.afk.kemonos.profile.data.IFavoritesRepository
import su.afk.kemonos.profile.domain.favorites.fresh.IFreshFavoriteArtistsUpdatesUseCase
import su.afk.kemonos.storage.api.repository.favorites.updates.IFreshFavoriteArtistsUpdatesRepository
import javax.inject.Inject

internal class GetFavoriteArtistsUseCase @Inject constructor(
    private val repository: IFavoritesRepository,
    private val computeFreshUpdates: IComputeFreshFavoriteArtistsUpdatesUseCase,
    private val freshUpdatesUseCase: IFreshFavoriteArtistsUpdatesUseCase,
    private val freshUpdatesStorage: IFreshFavoriteArtistsUpdatesRepository,
) : IGetFavoriteArtistsUseCase {

    @Volatile
    private var preloaded = false

    override suspend operator fun invoke(
        site: SelectedSite,
        checkDifferent: Boolean,
        refresh: Boolean,
    ): List<FavoriteArtist> {
        preloadFreshUpdatesIfNeeded()

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

        val now = System.currentTimeMillis()
        freshUpdatesStorage.replace(
            site = site,
            savedAtMs = now,
            items = updatedKeys,
        )
        freshUpdatesUseCase.set(site, updatedKeys)

        return network
    }

    private suspend fun preloadFreshUpdatesIfNeeded() {
        if (preloaded) return
        val items = freshUpdatesStorage.getAllActive()

        freshUpdatesUseCase.clearAll()
        items.groupBy { it.site }
            .forEach { (site, siteItems) ->
                freshUpdatesUseCase.set(
                    site = site,
                    items = siteItems.mapTo(mutableSetOf()) { stored ->
                        FreshFavoriteArtistKey(
                            name = stored.name,
                            service = stored.service,
                            id = stored.id,
                        )
                    }
                )
            }

        preloaded = true
    }
}
