package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun SearchPostsNavigation() {
    SearchPostsScreen(viewModel = hiltViewModel<SearchPostsViewModel>())
}