package su.afk.kemonos.common.presenter.screens.postsScreen.paging

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.error.view.DefaultErrorContent
import su.afk.kemonos.common.presenter.baseScreen.DefaultEmptyContent
import su.afk.kemonos.common.presenter.baseScreen.DefaultLoadingContent
import su.afk.kemonos.common.presenter.screens.postsScreen.PostsSource
import su.afk.kemonos.common.presenter.screens.postsScreen.ProfilePostsGrid
import su.afk.kemonos.domain.domain.models.ErrorItem
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.domain.domain.models.Tag

@Composable
fun PostsTabContent(
    posts: LazyPagingItems<PostDomain>,
    gridState: LazyGridState,
    currentTag: Tag?,
    onPostClick: (PostDomain) -> Unit,
    onRetry: () -> Unit,
    showFavCount: Boolean = false,
    parseError: (Throwable) -> ErrorItem,
) {
    ProfilePostsGrid(
        source = PostsSource.Paging(posts),
        postClick = onPostClick,
        showFavCount = showFavCount,
        gridState = gridState,
        appendLoadState = posts.loadState.append,
        onRetryAppend = { posts.retry() },
        parseError = parseError
    )

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
