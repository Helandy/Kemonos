package su.afk.kemonos.preferences.useCase

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.slug

object CacheKeys {
    const val FAVORITES_ARTISTS = "favorites_artists_cache_time"
    const val FAVORITES_POSTS = "favorites_posts_cache_time"

    /** "kemono_creators_cache_time" — слаг источника задаёт исторические имена ключей. */
    fun creators(site: SelectedSite): String = "${site.slug}_creators_cache_time"

    fun tags(site: SelectedSite): String = "${site.slug}_tags_cache_time"
}
