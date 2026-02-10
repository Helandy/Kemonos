package su.afk.kemonos.storage.api.repository.favorites.updates

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FreshFavoriteArtistKey

data class StoredFreshFavoriteArtistKey(
    val site: SelectedSite,
    val name: String,
    val service: String,
    val id: String,
    val savedAtMs: Long,
)

interface IFreshFavoriteArtistsUpdatesRepository {
    suspend fun getAllActive(): List<StoredFreshFavoriteArtistKey>
    suspend fun replace(site: SelectedSite, savedAtMs: Long, items: Set<FreshFavoriteArtistKey>)
    suspend fun clear(site: SelectedSite)
    suspend fun clearExpired()
}
