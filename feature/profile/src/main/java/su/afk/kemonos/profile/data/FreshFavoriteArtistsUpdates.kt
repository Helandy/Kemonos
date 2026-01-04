package su.afk.kemonos.profile.data

import su.afk.kemonos.domain.SelectedSite

data class FreshFavoriteArtistKey(
    val name: String,
    val service: String,
    val id: String,
)

object FreshFavoriteArtistsUpdates {
    private val map = mutableMapOf<SelectedSite, Set<FreshFavoriteArtistKey>>()

    fun set(site: SelectedSite, items: Set<FreshFavoriteArtistKey>) {
        map[site] = items
    }

    fun get(site: SelectedSite): Set<FreshFavoriteArtistKey> = map[site].orEmpty()

    fun totalCount(): Int = map.values.sumOf { it.size }

    fun clear(site: SelectedSite) {
        map.remove(site)
    }

    fun clearAll() {
        map.clear()
    }
}
