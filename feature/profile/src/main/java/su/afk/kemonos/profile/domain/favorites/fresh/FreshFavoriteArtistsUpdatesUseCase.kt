package su.afk.kemonos.profile.domain.favorites.fresh

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FreshFavoriteArtistKey
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

interface IFreshFavoriteArtistsUpdatesUseCase {
    fun set(site: SelectedSite, items: Set<FreshFavoriteArtistKey>)
    fun get(site: SelectedSite): Set<FreshFavoriteArtistKey>
    fun totalCount(): Int
    fun clear(site: SelectedSite)
    fun clearAll()
}

@Singleton
internal class FreshFavoriteArtistsUpdatesUseCase @Inject constructor() : IFreshFavoriteArtistsUpdatesUseCase {

    private val map = ConcurrentHashMap<SelectedSite, Set<FreshFavoriteArtistKey>>()

    override fun set(site: SelectedSite, items: Set<FreshFavoriteArtistKey>) {
        if (items.isEmpty()) {
            map.remove(site)
        } else {
            map[site] = items
        }
    }

    override fun get(site: SelectedSite): Set<FreshFavoriteArtistKey> = map[site].orEmpty()

    override fun totalCount(): Int = map.values.sumOf { it.size }

    override fun clear(site: SelectedSite) {
        map.remove(site)
    }

    override fun clearAll() {
        map.clear()
    }
}
