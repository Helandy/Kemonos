package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun PopularPostsNavigation() {
    PopularPostsScreen(viewModel = hiltViewModel<PopularPostsViewModel>())
}