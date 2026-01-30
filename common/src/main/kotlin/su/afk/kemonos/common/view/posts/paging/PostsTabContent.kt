package su.afk.kemonos.common.view.posts.paging

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.error.LocalErrorMapper
import su.afk.kemonos.common.error.view.DefaultErrorContent
import su.afk.kemonos.common.presenter.baseScreen.DefaultEmptyContent
import su.afk.kemonos.common.presenter.baseScreen.DefaultLoadingContent
import su.afk.kemonos.common.view.posts.grid.PostsGridPaging
import su.afk.kemonos.common.view.posts.grid.PostsSource
import su.afk.kemonos.common.view.posts.list.PostsListPaging
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.Tag
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.preferences.ui.PostsViewMode

@Composable
fun PostsTabContent(
    postsViewMode: PostsViewMode,
    dateMode: DateFormatMode,
    posts: LazyPagingItems<PostDomain>,
    gridState: LazyGridState,
    currentTag: Tag?,
    onPostClick: (PostDomain) -> Unit,
    onRetry: () -> Unit,
    showFavCount: Boolean = false,
) {
    val errorMapper = LocalErrorMapper.current

    when (postsViewMode) {
        PostsViewMode.GRID -> {
            PostsGridPaging(
                dateMode = dateMode,
                source = PostsSource.Paging(posts),
                postClick = onPostClick,
                showFavCount = showFavCount,
                gridState = gridState,
                appendLoadState = posts.loadState.append,
                onRetryAppend = { posts.retry() },
                parseError = errorMapper::map
            )
        }

        PostsViewMode.LIST -> {
            PostsListPaging(
                dateMode = dateMode,
                posts = posts,
                onPostClick = onPostClick,
                showFavCount = showFavCount,
                appendLoadState = posts.loadState.append,
                onRetryAppend = { posts.retry() },
                parseError = errorMapper::map
            )
        }
    }

    when (val refresh = posts.loadState.refresh) {
        is LoadState.Loading -> DefaultLoadingContent()
        is LoadState.Error -> DefaultErrorContent(
            errorItem = errorMapper.map(refresh.error),
            onRetry = onRetry
        )
        is LoadState.NotLoading -> {
            if (posts.itemCount == 0 && currentTag == null) {
                DefaultEmptyContent()
            }
        }
    }
}
