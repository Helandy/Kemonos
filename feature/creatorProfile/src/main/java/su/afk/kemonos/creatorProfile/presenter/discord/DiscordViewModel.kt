package su.afk.kemonos.creatorProfile.presenter.discord

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorProfile.domain.useCase.GetDiscordCommunityChannelsUseCase
import su.afk.kemonos.creatorProfile.navigation.CreatorDestination
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.delegates.NavigationDelegate
import su.afk.kemonos.creatorProfile.presenter.discord.DiscordState.*
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.shared.ShareLinkBuilder
import su.afk.kemonos.ui.shared.model.ShareTarget

internal class DiscordViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorDestination.CreatorProfile,
    private val navManager: NavigationManager,
    private val navigationDelegate: NavigationDelegate,
    private val getDiscordCommunityChannelsUseCase: GetDiscordCommunityChannelsUseCase,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    companion object {
        private const val DISCORD_SERVICE = "discord"
    }

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorDestination.CreatorProfile): DiscordViewModel
    }

    override fun createInitialState(): State = State()

    override fun onEvent(event: Event) {
        when (event) {
            Event.Retry -> loadChannels()
            Event.Back -> navManager.back()
            Event.CopyProfileLink -> copyProfileLink()
            is Event.SearchTextChanged -> setSearchText(event.text)
            is Event.OpenChannel -> openChannel(event.channelId)
        }
    }

    init {
        observeUiSetting()

        setState {
            copy(
                service = dest.service,
                creatorId = dest.id
            )
        }

        if (currentState.service.equals(DISCORD_SERVICE, ignoreCase = true) &&
            currentState.creatorId.isNotBlank()
        ) {
            loadChannels()
        }
    }

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadChannels() = viewModelScope.launch {
        if (currentState.loading) return@launch
        if (currentState.service.isBlank() || currentState.creatorId.isBlank()) return@launch

        setState { copy(loading = true, channels = emptyList()) }
        try {
            val community = getDiscordCommunityChannelsUseCase(currentState.creatorId)
            setState {
                copy(
                    channels = community.channels,
                    serverName = community.serverName.orEmpty(),
                    updated = community.updated
                )
            }
        } finally {
            setState { copy(loading = false) }
        }
    }

    private fun openChannel(channelId: String) {
        if (channelId.isBlank()) return
        val channel = currentState.channels.firstOrNull { it.channelId == channelId } ?: return
        navigationDelegate.navigateToCommunityChat(
            service = currentState.service,
            creatorId = currentState.creatorId,
            channel = channel
        )
    }

    private fun setSearchText(text: String) {
        if (text == currentState.searchText) return
        setState { copy(searchText = text) }
    }

    private fun copyProfileLink() {
        val siteRoot = getCurrentSiteRootUrlUseCase().trimEnd('/')
        val url = if (currentState.service.equals(DISCORD_SERVICE, ignoreCase = true)) {
            "$siteRoot/${currentState.service}/server/${currentState.creatorId}"
        } else {
            ShareLinkBuilder.build(
                ShareTarget.Profile(
                    siteRoot = siteRoot,
                    service = currentState.service,
                    userId = currentState.creatorId
                )
            )
        }
        setEffect(Effect.CopyPostLink(url))
    }
}
