package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.common.R
import su.afk.kemonos.common.components.posts.PostsContentPaging
import su.afk.kemonos.common.components.searchBar.PostsSearchBarWithMediaFilters
import su.afk.kemonos.common.error.LocalErrorMapper
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.profile.presenter.favoritePosts.FavoritePostsState.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritePostsScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
) {
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }
    LocalErrorMapper.current

    val focusManager = LocalFocusManager.current

    val posts = state.posts.collectAsLazyPagingItems()

    val pullState = rememberPullToRefreshState()
    val refreshing = state.loading

    val pagingIsEmpty = posts.loadState.refresh is LoadState.NotLoading && posts.itemCount == 0

    BaseScreen(
        contentPadding = PaddingValues(horizontal = 8.dp),
        topBarScroll = TopBarScroll.EnterAlways,
        isScroll = false,
        topBar = {
            PostsSearchBarWithMediaFilters(
                query = state.searchQuery,
                onQueryChange = { onEvent(Event.SearchQueryChanged(it)) },
                mediaFilter = state.mediaFilter,
                onToggleHasVideo = { onEvent(Event.ToggleHasVideo) },
                onToggleHasAttachments = { onEvent(Event.ToggleHasAttachments) },
                onToggleHasImages = { onEvent(Event.ToggleHasImages) },
                label = stringResource(R.string.search),
                onSearch = { focusManager.clearFocus() }
            )
        },
        isEmpty = !refreshing && pagingIsEmpty,
        onRetry = { onEvent(Event.Load()) },
    ) {
        PullToRefreshBox(
            state = pullState,
            isRefreshing = refreshing,
            onRefresh = {
                onEvent(Event.Load(refresh = true))
                posts.refresh()
            },
        ) {
            PostsContentPaging(
                uiSettingModel = state.uiSettingModel,
                posts = posts,
                onPostClick = { onEvent(Event.NavigateToPost(it)) },
                gridState = gridState,
                showFavCount = false,
                currentTag = null,
                onRetry = { posts.refresh() },
            )
        }
    }
}
