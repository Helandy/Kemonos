package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType

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
        groupByAuthor: Boolean,
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
