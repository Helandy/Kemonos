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
import su.afk.kemonos.preferences.ui.TranslateTarget
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.translate.TextTranslator
import su.afk.kemonos.ui.translate.preprocessForTranslation

internal class CommunityChatViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorDest.CommunityChat?,
    private val navManager: NavigationManager,
    private val navigationDelegate: NavigationDelegate,
    private val getProfileCommunityMessagesUseCase: GetProfileCommunityMessagesUseCase,
    private val translator: TextTranslator,
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
            is Event.ToggleTranslate -> onToggleTranslate(event.messageId, event.text)
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

    private fun onToggleTranslate(messageId: String, rawText: String) {
        if (messageId.isBlank()) return
        val plainText = rawText.preprocessForTranslation()
        if (plainText.isBlank()) return

        val isExpanded = messageId in currentState.translateExpandedIds
        val nextExpanded = !isExpanded
        setState {
            copy(
                translateExpandedIds = if (nextExpanded) {
                    translateExpandedIds + messageId
                } else {
                    translateExpandedIds - messageId
                }
            )
        }
        if (!nextExpanded) return

        when (currentState.uiSettingModel.translateTarget) {
            TranslateTarget.GOOGLE -> {
                setState { copy(translateExpandedIds = translateExpandedIds - messageId) }
                setEffect(
                    Effect.OpenGoogleTranslate(
                        text = plainText,
                        targetLangTag = currentState.uiSettingModel.translateLanguageTag
                    )
                )
                return
            }

            TranslateTarget.APP -> Unit
        }

        if (messageId in currentState.translateLoadingIds) return
        val cached = currentState.translatedTextById[messageId]
        if (!cached.isNullOrBlank() && currentState.translateErrorById[messageId].isNullOrBlank()) return

        viewModelScope.launch {
            setState {
                copy(
                    translateLoadingIds = translateLoadingIds + messageId,
                    translateErrorById = translateErrorById - messageId
                )
            }
            runCatching {
                translator.translateAuto(
                    text = plainText,
                    targetLangTag = currentState.uiSettingModel.translateLanguageTag
                )
            }.onSuccess { translated ->
                setState {
                    copy(
                        translateLoadingIds = translateLoadingIds - messageId,
                        translatedTextById = translatedTextById + (messageId to translated)
                    )
                }
            }.onFailure { e ->
                setState {
                    copy(
                        translateLoadingIds = translateLoadingIds - messageId,
                        translateErrorById = translateErrorById + (messageId to (e.message ?: "Translation error"))
                    )
                }
            }
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
