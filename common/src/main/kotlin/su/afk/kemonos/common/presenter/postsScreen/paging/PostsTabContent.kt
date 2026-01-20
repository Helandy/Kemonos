package su.afk.kemonos.common.presenter.postsScreen.paging

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.error.view.DefaultErrorContent
import su.afk.kemonos.common.presenter.baseScreen.DefaultEmptyContent
import su.afk.kemonos.common.presenter.baseScreen.DefaultLoadingContent
import su.afk.kemonos.common.presenter.postsScreen.grid.PostsGridPaging
import su.afk.kemonos.common.presenter.postsScreen.grid.PostsSource
import su.afk.kemonos.common.presenter.postsScreen.list.PostsListPaging
import su.afk.kemonos.domain.models.ErrorItem
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
    parseError: (Throwable) -> ErrorItem,
) {
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
                parseError = parseError
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
                parseError = parseError
            )
        }
    }

    when (val refresh = posts.loadState.refresh) {
        is LoadState.Loading -> DefaultLoadingContent()
        is LoadState.Error -> DefaultErrorContent(
            errorItem = parseError(refresh.error),
            onRetry = onRetry
        )
        is LoadState.NotLoading -> {
            if (posts.itemCount == 0 && currentTag == null) {
                DefaultEmptyContent()
            }
        }
    }
}
