package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class SearchPostsState {
    data class State(
        /** Поиск постов */
        val searchQuery: String = "",
        val mediaFilter: PostMediaFilter = PostMediaFilter(),
        val posts: Flow<PagingData<PostDomain>> = emptyFlow(),

        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class SearchQueryChanged(val value: String) : Event
        data class NavigateToPost(val post: PostDomain) : Event
        data object RandomPost : Event
        data object SwitchSite : Event

        data object ToggleHasVideo : Event
        data object ToggleHasAttachments : Event
        data object ToggleHasImages : Event
    }

    sealed interface Effect : UiEffect
}
