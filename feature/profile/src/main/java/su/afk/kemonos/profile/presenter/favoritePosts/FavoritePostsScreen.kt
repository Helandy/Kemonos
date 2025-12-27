package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.screens.postsScreen.PostsSource
import su.afk.kemonos.common.presenter.screens.postsScreen.ProfilePostsGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritePostsScreen(viewModel: FavoritePostsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BaseScreen(
        isScroll = false,
        isLoading = state.loading,
        isEmpty = !state.loading && state.allFavoritePosts.isEmpty()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            ProfilePostsGrid(
                source = PostsSource.Static(state.allFavoritePosts),
                postClick = { post ->
                    viewModel.navigateToPost(post)
                },
            )
        }
    }
}