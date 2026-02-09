package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.UiSettingModel

internal class FavoritePostsState {
    data class State(
        val selectSite: SelectedSite = SelectedSite.K,
        val loading: Boolean = false,
        val uiSettingModel: UiSettingModel = UiSettingModel(),

        val searchQuery: String = "",
        val posts: Flow<PagingData<PostDomain>> = emptyFlow(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class SearchQueryChanged(val query: String) : Event
        data class Load(val refresh: Boolean = false) : Event
        data class NavigateToPost(val post: PostDomain) : Event
    }

    sealed interface Effect : UiEffect
}
