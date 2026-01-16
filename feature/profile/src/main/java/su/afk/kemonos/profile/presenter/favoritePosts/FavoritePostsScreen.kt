package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.compose.foundation.layout.*
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
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.postsScreen.grid.PostsGrid
import su.afk.kemonos.common.presenter.postsScreen.grid.PostsSource
import su.afk.kemonos.common.presenter.postsScreen.list.PostsList
import su.afk.kemonos.preferences.ui.PostsViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritePostsScreen(viewModel: FavoritePostsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }

    val focusManager = LocalFocusManager.current

    val pullState = rememberPullToRefreshState()
    val refreshing = state.loading

    BaseScreen(
        contentPadding = PaddingValues(horizontal = 8.dp),
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = { scrollBehavior ->
            StandardTopBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
            ) {
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
            }
        },
        isLoading = state.loading,
        isEmpty = !state.loading && state.favoritePosts.isEmpty(),
        onRetry = viewModel::load,
    ) {
        PullToRefreshBox(
            state = pullState,
            isRefreshing = refreshing,
            onRefresh = { viewModel.load() }
        ) {
            when (state.uiSettingModel.favoritePostsViewMode) {
                PostsViewMode.GRID -> {
                    PostsGrid(
                        source = PostsSource.Static(state.favoritePosts),
                        postClick = { viewModel.navigateToPost(it) },
                        gridState = gridState,
                    )
                }

                PostsViewMode.LIST -> {
                    PostsList(
                        source = PostsSource.Static(state.favoritePosts),
                        onPostClick = { viewModel.navigateToPost(it) },
                    )
                }
            }
        }
    }
}