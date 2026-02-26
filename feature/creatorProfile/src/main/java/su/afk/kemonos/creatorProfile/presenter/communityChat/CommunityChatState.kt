package su.afk.kemonos.creatorProfile.presenter.communityChat

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class CommunityChatState {

    data class State(
        val service: String = "",
        val creatorId: String = "",
        val channelId: String = "",
        val channelName: String = "",
        val loading: Boolean = false,
        val loadingMore: Boolean = false,
        val canLoadMore: Boolean = false,
        val offset: Int = 0,
        val messages: List<CommunityMessage> = emptyList(),
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data object Back : Event
        data object LoadMore : Event
        data class OpenMedia(val url: String) : Event
        data class OpenUrl(val url: String) : Event
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
    }
}
