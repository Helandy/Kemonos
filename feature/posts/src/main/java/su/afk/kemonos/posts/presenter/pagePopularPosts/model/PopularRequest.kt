package su.afk.kemonos.posts.presenter.pagePopularPosts.model

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.domain.model.popular.Period

internal data class PopularRequest(
    val site: SelectedSite,
    val date: String?,
    val period: Period,
    val manualRefreshCounter: Long = 0L,
)
