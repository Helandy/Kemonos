package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class TagsSelectState {
    data class State(
        /** Выбранный тэг */
        val selectedTag: String? = null,

        /** Посты с тегом */
        val posts: Flow<PagingData<PostDomain>> = emptyFlow(),
        val mediaFilter: PostMediaFilter = PostMediaFilter(),

        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object Back : Event
        data object PullRefresh : Event
        data class NavigateToPost(val post: PostDomain) : Event
        data object ToggleHasVideo : Event
        data object ToggleHasAttachments : Event
        data object ToggleHasImages : Event
    }

    sealed interface Effect : UiEffect
}
