package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.postsScreen.paging.PostsTabContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TagsPostsScreen(
    viewModel: TagsSelectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val posts = state.posts.collectAsLazyPagingItems()
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }

    BaseScreen(
        isScroll = false,
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        /** Контент */
        PostsTabContent(
            dateMode = state.uiSettingModel.dateFormatMode,
            postsViewMode = state.uiSettingModel.tagsPostsViewMode,
            posts = posts,
            gridState = gridState,
            currentTag = null,
            onPostClick = viewModel::navigateToPost,
            onRetry = { posts.retry() },
            parseError = viewModel::parseError,
        )
    }
}