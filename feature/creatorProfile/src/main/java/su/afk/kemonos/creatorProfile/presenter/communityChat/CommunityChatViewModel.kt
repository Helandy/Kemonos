package su.afk.kemonos.creatorProfile.presenter.communityChat

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorProfile.domain.useCase.GetProfileCommunityMessagesUseCase
import su.afk.kemonos.creatorProfile.navigation.CreatorDest
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatState.*
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.delegates.NavigationDelegate
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew

internal class CommunityChatViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorDest.CommunityChat?,
    private val navManager: NavigationManager,
    private val navigationDelegate: NavigationDelegate,
    private val getProfileCommunityMessagesUseCase: GetProfileCommunityMessagesUseCase,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    companion object {
        private const val INITIAL_OFFSET = 0
    }

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorDest.CommunityChat): CommunityChatViewModel
    }

    override fun createInitialState(): State = State()

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Retry -> load(reset = true)
            Event.Back -> navManager.back()
            Event.LoadMore -> load(reset = false)
            is Event.OpenMedia -> navigationDelegate.navigateToOpenImage(event.url)
            is Event.OpenUrl -> setEffect(Effect.OpenUrl(event.url))
        }
    }

    init {
        observeUiSetting()

        setState {
            copy(
                service = dest?.service.orEmpty(),
                creatorId = dest?.creatorId.orEmpty(),
                channelId = dest?.channelId.orEmpty(),
                channelName = dest?.channelName.orEmpty(),
            )
        }

        if (currentState.channelId.isNotBlank()) {
            load(reset = true)
        }
    }

    private fun load(reset: Boolean) = viewModelScope.launch {
        if (currentState.channelId.isBlank() || currentState.service.isBlank()) return@launch
        if (currentState.loading || currentState.loadingMore) return@launch
        if (!reset && !currentState.canLoadMore) return@launch

        val offset = if (reset) INITIAL_OFFSET else currentState.offset
        setState {
            copy(
                loading = reset,
                loadingMore = !reset
            )
        }

        val page = getProfileCommunityMessagesUseCase(
            service = currentState.service,
            channelId = currentState.channelId,
            offset = offset
        )

        setState {
            copy(
                messages = if (reset) page else messages + page,
                offset = if (page.isNotEmpty()) getProfileCommunityMessagesUseCase.nextOffset(offset) else offset,
                canLoadMore = page.isNotEmpty(),
                loading = false,
                loadingMore = false
            )
        }
    }
}
