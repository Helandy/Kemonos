package su.afk.kemonos.profile.presenter.favoritePosts

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.domain.models.PostDomain

internal data class FavoritePostsState(
    val selectSite: SelectedSite = SelectedSite.K,

    val loading: Boolean = false,

    /** Все избранные посты */
    val allFavoritePosts: List<PostDomain> = emptyList(),
    val favoritePosts: List<PostDomain> = emptyList(),

    val searchQuery: String = "",
)