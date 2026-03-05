package su.afk.kemonos.posts.presenter.pageSearchPosts.model

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter

internal data class SearchLoadRequest(
    val site: SelectedSite,
    val search: String?,
    val mediaFilter: PostMediaFilter,
    val blacklistedAuthorKeys: Set<String>,
    val manualRefreshCounter: Long,
)
