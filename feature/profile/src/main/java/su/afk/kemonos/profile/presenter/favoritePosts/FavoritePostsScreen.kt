package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.screens.postsScreen.PostsSource
import su.afk.kemonos.common.presenter.screens.postsScreen.ProfilePostsGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritePostsScreen(viewModel: FavoritePostsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }

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
                )
            }
        },
        isLoading = state.loading,
        isEmpty = !state.loading && state.favoritePosts.isEmpty()
    ) {
        ProfilePostsGrid(
            source = PostsSource.Static(state.favoritePosts),
            postClick = { post ->
                viewModel.navigateToPost(post)
            },
            gridState = gridState,
        )
    }
}