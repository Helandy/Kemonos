package su.afk.kemonos.creatorProfile.presenter.communityChat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatState.*
import su.afk.kemonos.creatorProfile.presenter.communityChat.view.MessageItem
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.header.CreatorCenterBackTopBar
import su.afk.kemonos.deepLink.utils.openUrlInBrowser
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

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
    val listState = rememberLazyListState()

    LaunchedEffect(effect) {
        effect.collect {
            when (it) {
                is Effect.OpenUrl -> openUrlInBrowser(context, it.url)
            }
        }
    }
    LaunchedEffect(listState, state.messages.size, state.canLoadMore, state.loadingMore) {
        if (state.messages.isEmpty()) return@LaunchedEffect

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
                scrollBehavior = scrollBehavior
            )
        },
        isLoading = state.loading,
        onRetry = { onEvent(Event.Retry) },
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                items(state.messages.size) { index ->
                    MessageItem(
                        message = state.messages[index],
                        fallbackBaseUrl = fallbackBaseUrl,
                        dateMode = state.uiSettingModel.dateFormatMode,
                        onOpenMedia = { url -> onEvent(Event.OpenMedia(url)) },
                        onOpenUrl = { url -> onEvent(Event.OpenUrl(url)) },
                    )
                }

                item {
                    if (state.loadingMore) {
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
