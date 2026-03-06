package su.afk.kemonos.creatorProfile.presenter.communityChat

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.domain.useCase.GetDiscordCommunityMessagesUseCase
import su.afk.kemonos.creatorProfile.domain.useCase.GetProfileCommunityMessagesUseCase
import su.afk.kemonos.creatorProfile.navigation.CreatorDestination
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatState.*
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.CommunityMedia
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.delegates.NavigationDelegate
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.TranslateTarget
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.translate.TextTranslator
import su.afk.kemonos.ui.translate.preprocessForTranslation
import su.afk.kemonos.ui.uiUtils.format.isImageFile
import su.afk.kemonos.ui.uiUtils.format.isVideoFile

internal class CommunityChatViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorDestination.CommunityChat?,
    private val navManager: NavigationManager,
    private val navigationDelegate: NavigationDelegate,
    private val downloadUtil: IDownloadUtil,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val getProfileCommunityMessagesUseCase: GetProfileCommunityMessagesUseCase,
    private val getDiscordCommunityMessagesUseCase: GetDiscordCommunityMessagesUseCase,
    private val translator: TextTranslator,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {
    private var initialLoadStarted = false

    companion object {
        private const val DISCORD_SERVICE = "discord"
        private const val INITIAL_OFFSET = 0
        private const val DISCORD_PAGE_SIZE = 150
    }

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorDestination.CommunityChat): CommunityChatViewModel
    }

    override fun createInitialState(): State = State()

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                val shouldApplyInitialReverse = !initialLoadStarted && isDiscordService(currentState.service)
                setState {
                    copy(
                        uiSettingModel = model,
                        reverseOrder = if (shouldApplyInitialReverse) {
                            model.discordCommunityReverseOrderDefault
                        } else {
                            reverseOrder
                        }
                    )
                }
                if (!initialLoadStarted && currentState.channelId.isNotBlank()) {
                    initialLoadStarted = true
                    load(reset = true)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Retry -> load(reset = true)
            Event.Back -> navManager.back()
            Event.CopyChatLink -> copyChatLink()
            is Event.ListScrollChanged -> onListScrollChanged(event)
            Event.LoadMore -> load(reset = false)
            Event.ToggleReverseOrder -> toggleReverseOrder()
            is Event.OpenMedia -> onOpenMedia(event)
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
                channelPostCount = dest?.channelPostCount,
                reverseOrder = false,
            )
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

        val isDiscord = isDiscordService(currentState.service)
        val offset = when {
            reset && isDiscord && currentState.reverseOrder ->
                getDiscordInitialOffset(currentState.channelPostCount)

            reset -> INITIAL_OFFSET
            else -> currentState.offset
        }
        setState {
            copy(
                loading = reset,
                loadingMore = !reset
            )
        }

        val page = if (currentState.service.equals(DISCORD_SERVICE, ignoreCase = true)) {
            getDiscordCommunityMessagesUseCase(
                channelId = currentState.channelId,
                offset = offset
            )
        } else {
            getProfileCommunityMessagesUseCase(
                service = currentState.service,
                channelId = currentState.channelId,
                offset = offset
            )
        }

        setState {
            val hasPage = page.isNotEmpty()
            val nextOffset = when {
                !hasPage -> offset
                isDiscord && reverseOrder -> (offset - DISCORD_PAGE_SIZE).coerceAtLeast(INITIAL_OFFSET)
                else -> getProfileCommunityMessagesUseCase.nextOffset(offset)
            }
            val canLoadMoreNext = when {
                !hasPage -> false
                isDiscord && reverseOrder -> offset > INITIAL_OFFSET
                else -> true
            }
            val mergedMessages = if (reset) page else messages + page
            val orderedMessages = if (isDiscord) {
                mergedMessages.sortForDisplay(reverseOrder = reverseOrder)
            } else {
                mergedMessages
            }
            copy(
                messages = orderedMessages,
                offset = nextOffset,
                canLoadMore = canLoadMoreNext,
                loading = false,
                loadingMore = false
            )
        }
    }

    private fun toggleReverseOrder() {
        if (!isDiscordService(currentState.service)) return
        if (currentState.loading || currentState.loadingMore) return

        setState {
            copy(
                reverseOrder = !reverseOrder,
                messages = emptyList(),
                offset = INITIAL_OFFSET,
                canLoadMore = false,
            )
        }
        load(reset = true)
    }

    private fun onOpenMedia(event: Event.OpenMedia) {
        val media = event.media
        if (isImageFile(media.pathOrUrl)) {
            navigationDelegate.navigateToOpenImage(media.openUrl)
            return
        }
        if (isVideoFile(media.pathOrUrl)) {
            setEffect(Effect.OpenVideo(url = media.openUrl, fileName = resolveFileName(media)))
            return
        }

        viewModelScope.launch {
            runCatching {
                downloadUtil.enqueueSystemDownload(
                    url = media.openUrl,
                    fileName = resolveFileName(media),
                    service = currentState.service,
                    creatorName = currentState.creatorId,
                    postId = currentState.channelId,
                    postTitle = currentState.channelName,
                )
            }.onSuccess {
                setEffect(Effect.DownloadToast(resolveFileName(media)))
            }.onFailure { throwable ->
                setEffect(Effect.ShowToast(errorHandler.parse(throwable, navigate = false).message))
            }
        }
    }

    private fun resolveFileName(media: CommunityMedia): String {
        val explicitName = media.fileName?.trim().orEmpty()
        if (explicitName.isNotBlank()) return explicitName

        return media.openUrl
            .substringAfterLast('/')
            .substringBefore('?')
            .ifBlank { "file" }
    }

    private fun copyChatLink() {
        if (currentState.channelId.isBlank() || currentState.service.isBlank()) return

        val root = getCurrentSiteRootUrlUseCase().trimEnd('/')
        val link = if (isDiscordService(currentState.service)) {
            "$root/${currentState.service}/server/${currentState.creatorId}/${currentState.channelId}"
        } else {
            "$root/${currentState.service}/user/${currentState.creatorId}/community"
        }
        setEffect(Effect.CopyChatLink(link))
    }

    private fun onListScrollChanged(event: Event.ListScrollChanged) {
        if (event.firstVisibleItemIndex == currentState.listFirstVisibleItemIndex &&
            event.firstVisibleItemScrollOffset == currentState.listFirstVisibleItemScrollOffset
        ) {
            return
        }
        setState {
            copy(
                listFirstVisibleItemIndex = event.firstVisibleItemIndex,
                listFirstVisibleItemScrollOffset = event.firstVisibleItemScrollOffset
            )
        }
    }

    private fun isDiscordService(service: String): Boolean =
        service.equals(DISCORD_SERVICE, ignoreCase = true)

    private fun getDiscordInitialOffset(postCount: Int?): Int {
        val count = postCount ?: return INITIAL_OFFSET
        if (count <= DISCORD_PAGE_SIZE) return INITIAL_OFFSET
        return ((count - 1) / DISCORD_PAGE_SIZE) * DISCORD_PAGE_SIZE
    }

    private fun List<CommunityMessage>.sortForDisplay(
        reverseOrder: Boolean
    ): List<CommunityMessage> {
        val comparator = compareBy<CommunityMessage>(
            { it.createdAt },
            { it.messageId }
        )
        return if (reverseOrder) {
            sortedWith(comparator.reversed())
        } else {
            sortedWith(comparator)
        }
    }
}
