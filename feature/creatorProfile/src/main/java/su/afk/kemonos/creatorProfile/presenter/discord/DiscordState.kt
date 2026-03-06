package su.afk.kemonos.creatorProfile.presenter.discord

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class DiscordState {

    data class State(
        val service: String = "",
        val creatorId: String = "",
        val serverName: String = "",
        val updated: String? = null,
        val loading: Boolean = false,
        val searchText: String = "",
        val channels: List<CommunityChannel> = emptyList(),
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data object Back : Event
        data object CopyProfileLink : Event
        data class SearchTextChanged(val text: String) : Event
        data class OpenChannel(val channelId: String) : Event
    }

    sealed interface Effect : UiEffect {
        data class CopyPostLink(val message: String) : Effect
    }
}
