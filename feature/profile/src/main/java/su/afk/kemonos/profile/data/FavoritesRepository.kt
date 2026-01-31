package su.afk.kemonos.profile.data

import su.afk.kemonos.common.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.network.util.call
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType
import su.afk.kemonos.profile.data.api.FavoritesApi
import su.afk.kemonos.profile.data.dto.favorites.artist.FavoriteArtistDto.Companion.toDomain
import su.afk.kemonos.storage.api.repository.favorites.artist.IStoreFavoriteArtistsRepository
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import javax.inject.Inject

internal interface IFavoritesRepository {
    suspend fun pageFavoriteArtists(
        site: SelectedSite,
        service: String,
        query: String,
        sort: FavoriteSortedType,
        ascending: Boolean,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtist>

    suspend fun getDistinctServices(site: SelectedSite): List<String>

    suspend fun pageFavoritePosts(
        site: SelectedSite,
        query: String?,
        limit: Int,
        offset: Int,
    ): List<PostDomain>

    suspend fun getFavoriteArtists(
        site: SelectedSite,
        getOldCache: Boolean,
        forceRefresh: Boolean = false,
    ): List<FavoriteArtist>

    suspend fun getFavoritePosts(site: SelectedSite, refresh: Boolean): List<PostDomain>
    suspend fun refreshFavoriteArtists(site: SelectedSite): List<FavoriteArtist>
}

internal class FavoritesRepository @Inject constructor(
    private val api: FavoritesApi,
    private val artistsStore: IStoreFavoriteArtistsRepository,
    private val postsStore: IStoreFavoritePostsRepository,
) : IFavoritesRepository {

    override suspend fun pageFavoriteArtists(
        site: SelectedSite,
        service: String,
        query: String,
        sort: FavoriteSortedType,
        ascending: Boolean,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtist> {
        return artistsStore.page(
            site = site,
            service = service,
            query = query,
            sort = sort,
            ascending = ascending,
            limit = limit,
            offset = offset
        )
    }

    override suspend fun getDistinctServices(site: SelectedSite): List<String> =
        artistsStore.getDistinctServices(site)

    override suspend fun pageFavoritePosts(
        site: SelectedSite,
        query: String?,
        limit: Int,
        offset: Int,
    ): List<PostDomain> {
        val q = query?.trim().orEmpty()
        return if (q.length >= 2) {
            postsStore.pageSearch(site = site, query = q, limit = limit, offset = offset)
        } else {
            postsStore.page(site = site, limit = limit, offset = offset)
        }
    }

    override suspend fun getFavoriteArtists(
        site: SelectedSite,
        getOldCache: Boolean,
        forceRefresh: Boolean,
    ): List<FavoriteArtist> {
        if (getOldCache) return artistsStore.getAll(site)
        if (forceRefresh) return refreshFavoriteArtists(site)

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

    override suspend fun getFavoritePosts(site: SelectedSite, refresh: Boolean): List<PostDomain> {
        if (!refresh && postsStore.isCacheFresh(site)) {
            return postsStore.getAll(site)
        }

        return api.getFavoritePosts().call { list ->
            val network = list.map { it.toDomain() }

            if (network.isNotEmpty()) {
                postsStore.replaceAll(site, network)
            }

            network
        }
    }
}
