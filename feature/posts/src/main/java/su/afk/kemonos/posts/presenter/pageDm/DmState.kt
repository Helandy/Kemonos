package su.afk.kemonos.posts.presenter.pageDm

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.posts.domain.model.dms.DmDomain
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class DmState {
    data class State(
        val searchQuery: String = "",
        val dms: Flow<PagingData<DmDomain>> = emptyFlow(),
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class SearchQueryChanged(val value: String) : Event
        data object SearchSubmitted : Event
        data class NavigateToProfile(val service: String, val id: String) : Event
        data object SwitchSite : Event
    }

    sealed interface Effect : UiEffect
}
