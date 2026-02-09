package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.api.popular.PopularInfo
import su.afk.kemonos.posts.api.popular.PopularProps
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.popular.PopularNavSlot
import su.afk.kemonos.preferences.ui.UiSettingModel

internal class PopularPostsState {
    data class State(
        /** Популярные посты */
        val posts: Flow<PagingData<PostDomain>> = emptyFlow(),

        val popularPostsInfo: PopularInfo? = null,
        val popularProps: PopularProps? = null,

        val popularPeriod: Period = Period.RECENT,
        val popularDateForPopular: String? = null,

        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class LoadPopular(val date: String?, val period: Period) : Event
        data class PeriodSlotClick(val period: Period, val slot: PopularNavSlot) : Event
        data class NavigateToPost(val post: PostDomain) : Event
        data object SwitchSite : Event
    }

    sealed interface Effect : UiEffect
}
