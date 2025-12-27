package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.screens.postsScreen.paging.PostsTabContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TagsPostsScreen(
    viewModel: TagsSelectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val posts = state.posts.collectAsLazyPagingItems()

    BaseScreen(
        isScroll = false,
    ) {
        /** Контент */
        PostsTabContent(
            posts = posts,
            currentTag = null,
            onPostClick = viewModel::navigateToPost,
            onRetry = { posts.retry() },
            parseError = viewModel::parseError,
        )
    }
}