package su.afk.kemonos.storage.api.repository.favorites.artist

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType

interface IStoreFavoriteArtistsRepository {
    suspend fun page(
        site: SelectedSite,
        service: String,
        query: String,
        sort: FavoriteSortedType,
        ascending: Boolean,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtist>

    suspend fun getDistinctServices(site: SelectedSite): List<String>

    suspend fun getAll(site: SelectedSite): List<FavoriteArtist>
    suspend fun replaceAll(site: SelectedSite, items: List<FavoriteArtist>)
    suspend fun remove(site: SelectedSite, service: String, id: String)
    suspend fun clear(site: SelectedSite)
    suspend fun isCacheFresh(site: SelectedSite): Boolean
    suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean
}