package su.afk.kemonos.creatorProfile.presenter.communityChat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatState.*
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatState.State
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.MessageItemActions
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.MessageItemUi
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.MessageTranslationUi
import su.afk.kemonos.creatorProfile.presenter.communityChat.view.MessageItem
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.header.CreatorCenterBackTopBar
import su.afk.kemonos.deepLink.utils.openUrlInBrowser
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.shared.ShareActions
import su.afk.kemonos.ui.toast.toast
import su.afk.kemonos.ui.translate.openGoogleTranslate
import su.afk.kemonos.ui.video.openVideoExternally

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommunityChatScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>
) {
    val context = LocalContext.current
    val resolver = LocalDomainResolver.current
    val fallbackBaseUrl = resolver.baseUrlByService(state.service)
    val listState = remember(state.channelId) { LazyListState() }
    var menuExpanded by rememberSaveable(state.channelId) { mutableStateOf(false) }
    var searchEffectInitialized by rememberSaveable(state.channelId) { mutableStateOf(false) }
    var pendingScrollRestore by remember(state.channelId) { mutableStateOf(true) }
    var scrollSyncEnabled by remember(state.channelId) { mutableStateOf(false) }
    val isDiscord = remember(state.service) { state.service.equals("discord", ignoreCase = true) }
    val focusManager = LocalFocusManager.current
    val messageItemUi = remember(
        fallbackBaseUrl,
        state.uiSettingModel.dateFormatMode,
        isDiscord,
        state.uiSettingModel.autoplayCommunityVideo,
        state.translateExpandedIds,
        state.translateLoadingIds,
        state.translatedTextById,
        state.translateErrorById,
    ) {
        MessageItemUi(
            fallbackBaseUrl = fallbackBaseUrl,
            dateMode = state.uiSettingModel.dateFormatMode,
            showAuthorAvatar = isDiscord,
            autoplayVideoInline = state.uiSettingModel.autoplayCommunityVideo,
            translation = MessageTranslationUi(
                expandedIds = state.translateExpandedIds,
                loadingIds = state.translateLoadingIds,
                translatedTextById = state.translatedTextById,
                errorById = state.translateErrorById,
            )
        )
    }
    val messageItemActions = remember(onEvent) {
        MessageItemActions(
            onOpenMedia = { media -> onEvent(Event.OpenMedia(media)) },
            onOpenUrl = { url -> onEvent(Event.OpenUrl(url)) },
            onToggleTranslate = { messageId, text ->
                onEvent(Event.ToggleTranslate(messageId = messageId, text = text))
            }
        )
    }
    val filteredMessages = remember(state.messages, state.searchQuery) {
        val query = state.searchQuery.trim()
        if (query.isBlank()) {
            state.messages
        } else {
            val lowerQuery = query.lowercase()
            state.messages.filter { message ->
                message.text.orEmpty().contains(lowerQuery, ignoreCase = true) ||
                        message.userName.orEmpty().contains(lowerQuery, ignoreCase = true) ||
                        message.userId.contains(lowerQuery, ignoreCase = true) ||
                        message.attachments.any { attachment ->
                            attachment.name.orEmpty().contains(lowerQuery, ignoreCase = true)
                        }
            }
        }
    }

    LaunchedEffect(effect) {
        effect.collect {
            when (it) {
                is Effect.OpenUrl -> openUrlInBrowser(context, it.url)
                is Effect.CopyChatLink -> ShareActions.copyToClipboard(
                    context,
                    context.getString(R.string.copy_link),
                    it.message
                )
                is Effect.OpenVideo -> openVideoExternally(context, it.url, it.fileName)
                is Effect.ShowToast -> context.toast(it.message)
                is Effect.DownloadToast -> {
                    val safeName = it.fileName.trim().takeIf { name -> name.isNotBlank() }
                    val message = if (safeName != null) {
                        context.getString(R.string.download_started_named, safeName)
                    } else {
                        context.getString(R.string.download_started)
                    }
                    context.toast(message)
                }
                is Effect.OpenGoogleTranslate -> openGoogleTranslate(context, it.text, it.targetLangTag)
            }
        }
    }
    LaunchedEffect(state.searchQuery) {
        if (!searchEffectInitialized) {
            searchEffectInitialized = true
            return@LaunchedEffect
        }
        listState.scrollToItem(0)
    }

    LaunchedEffect(
        pendingScrollRestore,
        state.messages.size,
        state.listFirstVisibleItemIndex,
        state.listFirstVisibleItemScrollOffset
    ) {
        if (!pendingScrollRestore) return@LaunchedEffect

        val targetIndex = state.listFirstVisibleItemIndex.coerceAtLeast(0)
        val targetOffset = state.listFirstVisibleItemScrollOffset.coerceAtLeast(0)

        if (targetIndex == 0 && targetOffset == 0) {
            pendingScrollRestore = false
            scrollSyncEnabled = true
            return@LaunchedEffect
        }

        if (state.messages.isEmpty()) return@LaunchedEffect

        val boundedIndex = targetIndex.coerceAtMost(state.messages.lastIndex)
        listState.scrollToItem(
            index = boundedIndex,
            scrollOffset = if (boundedIndex == targetIndex) targetOffset else 0
        )
        pendingScrollRestore = false
        scrollSyncEnabled = true
    }

    LaunchedEffect(listState, scrollSyncEnabled) {
        if (!scrollSyncEnabled) return@LaunchedEffect
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                onEvent(
                    Event.ListScrollChanged(
                        firstVisibleItemIndex = index,
                        firstVisibleItemScrollOffset = offset
                    )
                )
            }
    }

    LaunchedEffect(listState, state.messages.size, state.canLoadMore, state.loadingMore, state.searchQuery) {
        if (state.messages.isEmpty()) return@LaunchedEffect
        if (state.searchQuery.isNotBlank()) return@LaunchedEffect

        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .filter { lastVisibleIndex ->
                val thresholdIndex = (state.messages.lastIndex - 3).coerceAtLeast(0)
                state.canLoadMore && !state.loadingMore && lastVisibleIndex >= thresholdIndex
            }
            .collect { onEvent(Event.LoadMore) }
    }

    BaseScreen(
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CreatorCenterBackTopBar(
                title = state.channelName,
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.share)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.share)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onEvent(Event.CopyChatLink)
                            }
                        )
                    }
                }
            )
        },
        isLoading = state.loading,
        onRetry = { onEvent(Event.Retry) },
        floatingActionButtonEnd = {}
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { onEvent(Event.SearchQueryChanged(it)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text(stringResource(R.string.search)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotBlank()) {
                                IconButton(onClick = { onEvent(Event.SearchQueryChanged("")) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { focusManager.clearFocus() }
                        )
                    )

                    if (isDiscord) {
                        FilledTonalIconButton(
                            onClick = { onEvent(Event.ToggleReverseOrder) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = if (state.reverseOrder) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = if (state.reverseOrder) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Reverse order"
                            )
                        }
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    items(filteredMessages.size) { index ->
                        MessageItem(
                            message = filteredMessages[index],
                            ui = messageItemUi,
                            actions = messageItemActions,
                        )
                    }

                    item {
                        if (state.loadingMore && state.searchQuery.isBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
