package su.afk.kemonos.profile.data

import su.afk.kemonos.common.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.network.util.call
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.profile.data.api.FavoritesApi
import su.afk.kemonos.profile.data.dto.favorites.artist.FavoriteArtistDto.Companion.toDomain
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import javax.inject.Inject

internal interface IFavoritesRepository {
    suspend fun getFavoriteArtists(site: SelectedSite): List<FavoriteArtist>
    suspend fun getFavoritePosts(site: SelectedSite): List<PostDomain>
    suspend fun refreshFavoriteArtists(site: SelectedSite): List<FavoriteArtist>
}

internal class FavoritesRepository @Inject constructor(
    private val api: FavoritesApi,
    private val artistsStore: IStoreFavoriteArtistsUseCase,
    private val postsStore: IStoreFavoritePostsUseCase,
) : IFavoritesRepository {

    override suspend fun getFavoriteArtists(site: SelectedSite): List<FavoriteArtist> {
        if (artistsStore.isCacheFresh(site)) return artistsStore.getAll(site)
        return refreshFavoriteArtists(site)
    }

    override suspend fun refreshFavoriteArtists(site: SelectedSite): List<FavoriteArtist> {
        api.getFavoriteArtists().call { list ->
            val network = list.map { it.toDomain() }

            if (network.isNotEmpty()) {
                artistsStore.replaceAll(site, network)
            }
            return network
        }
    }

    override suspend fun getFavoritePosts(site: SelectedSite): List<PostDomain> {
        if (postsStore.isCacheFresh(site)) return postsStore.getAll(site)

        return api.getFavoritePosts().call { list ->
            val network = list.map { it.toDomain() }

            if (network.isNotEmpty()) {
                postsStore.replaceAll(site, network)
            }

            network
        }
    }
}
