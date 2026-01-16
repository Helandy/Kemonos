package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.UiSettingModel

internal data class SearchPostsState(
    /** Поиск постов */
    val searchQuery: String = "",
    val posts: Flow<PagingData<PostDomain>> = emptyFlow(),

    val uiSettingModel: UiSettingModel = UiSettingModel(),
)