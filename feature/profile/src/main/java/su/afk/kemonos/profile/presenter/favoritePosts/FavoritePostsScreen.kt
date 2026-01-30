package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import su.afk.kemonos.common.R
import su.afk.kemonos.common.error.LocalErrorMapper
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.view.posts.PostsGridPaging
import su.afk.kemonos.common.view.posts.PostsListPaging
import su.afk.kemonos.preferences.ui.PostsViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritePostsScreen(viewModel: FavoritePostsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }
    val errorMapper = LocalErrorMapper.current

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
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text(stringResource(R.string.search)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                )
            )
        },
        isEmpty = !refreshing && pagingIsEmpty,
        onRetry = viewModel::load,
    ) {
        PullToRefreshBox(
            state = pullState,
            isRefreshing = refreshing,
            onRefresh = {
                viewModel.load(refresh = true)
                posts.refresh()
            },
        ) {
            when (state.uiSettingModel.favoritePostsViewMode) {
                PostsViewMode.GRID -> {
                    PostsGridPaging(
                        dateMode = state.uiSettingModel.dateFormatMode,
                        posts = posts,
                        postClick = viewModel::navigateToPost,
                        gridState = gridState,
                        showFavCount = false,
                        appendLoadState = posts.loadState.append,
                        onRetryAppend = { posts.refresh() },
                        parseError = errorMapper::map
                    )
                }

                PostsViewMode.LIST -> {
                    PostsListPaging(
                        dateMode = state.uiSettingModel.dateFormatMode,
                        posts = posts,
                        onPostClick = viewModel::navigateToPost,
                        showFavCount = false,
                        appendLoadState = posts.loadState.append,
                        onRetryAppend = { posts.refresh() },
                        parseError = errorMapper::map
                    )
                }
            }
        }
    }
}