package su.afk.kemonos.creatorProfile.data

import retrofit2.HttpException
import su.afk.kemonos.creatorProfile.data.api.FavoritesCreatorApi
import su.afk.kemonos.creatorProfile.domain.repository.IFavoritesCreatorRepository
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.IRefreshFavoriteArtistsUseCase
import su.afk.kemonos.storage.api.repository.favorites.artist.IStoreFavoriteArtistsRepository
import javax.inject.Inject

internal class FavoritesCreatorRepository @Inject constructor(
    private val api: FavoritesCreatorApi,
    private val store: IStoreFavoriteArtistsRepository,
    private val refresher: IRefreshFavoriteArtistsUseCase,
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