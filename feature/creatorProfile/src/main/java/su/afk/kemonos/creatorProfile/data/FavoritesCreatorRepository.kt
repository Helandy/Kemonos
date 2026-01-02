package su.afk.kemonos.creatorProfile.data

import su.afk.kemonos.creatorProfile.data.api.FavoritesCreatorApi
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.network.util.successOrFalse
import su.afk.kemonos.profile.api.domain.IRefreshFavoriteArtists
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import javax.inject.Inject

internal interface IFavoritesCreatorRepository {
    suspend fun addCreator(site: SelectedSite, service: String, id: String): Boolean
    suspend fun removeCreator(site: SelectedSite, service: String, id: String): Boolean
}

internal class FavoritesCreatorRepository @Inject constructor(
    private val api: FavoritesCreatorApi,
    private val store: IStoreFavoriteArtistsUseCase,
    private val refresher: IRefreshFavoriteArtists,
) : IFavoritesCreatorRepository {

    override suspend fun addCreator(site: SelectedSite, service: String, id: String): Boolean {
        val response = api.addFavoriteCreator(service = service, id = id).successOrFalse()
        if (!response) return false

        runCatching { refresher.refreshFavoriteArtists(site = site) }
        return true
    }

    override suspend fun removeCreator(site: SelectedSite, service: String, id: String): Boolean {
        val response = api.removeFavoriteCreator(service = service, id = id).successOrFalse()
        if (!response) return false

        store.remove(site, service, id)

        return true
    }
}