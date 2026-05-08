package su.afk.kemonos.creatorProfile.presenter.discord

import kotlinx.serialization.Serializable
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.navigation.CreatorDestination
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

@Serializable
internal data class DiscordPersistedState(
    val service: String,
    val creatorId: String,
    val serverName: String = "",
    val searchText: String = "",
) {
    companion object {
        internal fun fromDest(dest: CreatorDestination.CreatorProfile): DiscordPersistedState =
            DiscordPersistedState(
                service = dest.service,
                creatorId = dest.id,
            )
    }
}

internal fun DiscordPersistedState.toState(): DiscordState.State =
    DiscordState.State(
        service = service,
        creatorId = creatorId,
        serverName = serverName,
        searchText = searchText,
    )

internal fun DiscordState.State.toPersistedState(): DiscordPersistedState =
    DiscordPersistedState(
        service = service,
        creatorId = creatorId,
        serverName = serverName,
        searchText = searchText,
    )
