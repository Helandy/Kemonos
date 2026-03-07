package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.posts.presenter.tagsSelect.TagsSelectState.*
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilterChips
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TagsPostsScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
) {
    val posts = state.posts.collectAsLazyPagingItems()
    val pullState = rememberPullToRefreshState()
    val isRefreshing = posts.loadState.refresh is LoadState.Loading
    val isEmptyResult = posts.itemCount == 0 && posts.loadState.refresh !is LoadState.Loading
    val topBarScrollMode = if (isEmptyResult) TopBarScroll.Pinned else TopBarScroll.EnterAlways

    BaseScreen(
        isScroll = false,
        contentPadding = PaddingValues(horizontal = 8.dp),
        topBarScroll = topBarScrollMode,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = state.selectedTag ?: stringResource(R.string.tags),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize(),
            state = pullState,
            isRefreshing = isRefreshing,
            onRefresh = { onEvent(Event.PullRefresh) },
        ) {
            PostsContentPaging(
                postsViewMode = state.uiSettingModel.tagsPostsViewMode,
                uiSettingModel = state.uiSettingModel,
                gridPostsSize = state.uiSettingModel.tagsPostsGridSize,
                posts = posts,
                currentTag = null,
                onPostClick = { onEvent(Event.NavigateToPost(it)) },
                onRetry = { posts.retry() },
                header = {
                    PostMediaFilterChips(
                        filter = state.mediaFilter,
                        onToggleHasVideo = { onEvent(Event.ToggleHasVideo) },
                        onToggleHasAttachments = { onEvent(Event.ToggleHasAttachments) },
                        onToggleHasImages = { onEvent(Event.ToggleHasImages) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    )
                }
            )
        }
    }
}
