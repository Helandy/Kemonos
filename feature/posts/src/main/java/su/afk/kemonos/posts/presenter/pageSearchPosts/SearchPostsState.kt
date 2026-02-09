package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.UiSettingModel

internal class SearchPostsState {
    data class State(
        /** Поиск постов */
        val searchQuery: String = "",
        val posts: Flow<PagingData<PostDomain>> = emptyFlow(),

        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class SearchQueryChanged(val value: String) : Event
        data class NavigateToPost(val post: PostDomain) : Event
        data object RandomPost : Event
        data object SwitchSite : Event
    }

    sealed interface Effect : UiEffect
}
