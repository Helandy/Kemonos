package su.afk.kemonos.creatorProfile.data

import retrofit2.HttpException
import su.afk.kemonos.creatorProfile.data.api.FavoritesCreatorApi
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.IRefreshFavoriteArtists
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import javax.inject.Inject

internal interface IFavoritesCreatorRepository {
    suspend fun addCreator(site: SelectedSite, service: String, id: String): Result<Unit>
    suspend fun removeCreator(site: SelectedSite, service: String, id: String): Result<Unit>
}

internal class FavoritesCreatorRepository @Inject constructor(
    private val api: FavoritesCreatorApi,
    private val store: IStoreFavoriteArtistsUseCase,
    private val refresher: IRefreshFavoriteArtists,
) : IFavoritesCreatorRepository {

    override suspend fun addCreator(site: SelectedSite, service: String, id: String): Result<Unit> = runCatching {
        val resp = api.addFavoriteCreator(service = service, id = id)
        if (!resp.isSuccessful) throw HttpException(resp)

        runCatching { refresher.refreshFavoriteArtists(site = site) }
    }

    override suspend fun removeCreator(site: SelectedSite, service: String, id: String): Result<Unit> = runCatching {
        val resp = api.removeFavoriteCreator(service = service, id = id)
        if (!resp.isSuccessful) throw HttpException(resp)

        store.remove(site, service, id)
    }
}