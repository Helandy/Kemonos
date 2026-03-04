package su.afk.kemonos.posts.presenter.pageDm.model

import su.afk.kemonos.domain.SelectedSite

internal data class DmLoadRequest(
    val site: SelectedSite,
    val query: String,
    val manualRefreshCounter: Long,
)
