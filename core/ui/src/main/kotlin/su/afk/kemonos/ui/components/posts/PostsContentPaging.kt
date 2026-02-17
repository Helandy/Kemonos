package su.afk.kemonos.ui.components.posts

import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.Tag
import su.afk.kemonos.error.error.LocalErrorMapper
import su.afk.kemonos.error.error.view.DefaultErrorContent
import su.afk.kemonos.preferences.ui.PostsViewMode
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseScreen.DefaultEmptyContent
import su.afk.kemonos.ui.presenter.baseScreen.DefaultLoadingContent

@Composable
fun PostsContentPaging(
    uiSettingModel: UiSettingModel,
    postsViewMode: PostsViewMode,
    posts: LazyPagingItems<PostDomain>,
    currentTag: Tag?,
    onPostClick: (PostDomain) -> Unit,
    onRetry: () -> Unit,
    showFavCount: Boolean = false,
) {
    val errorMapper = LocalErrorMapper.current

    when (postsViewMode) {
        PostsViewMode.GRID -> {
            PostsGridPaging(
                uiSettingModel = uiSettingModel,
                posts = posts,
                postClick = onPostClick,
                showFavCount = showFavCount,
                appendLoadState = posts.loadState.append,
                onRetryAppend = { posts.retry() },
                parseError = errorMapper::map
            )
        }

        PostsViewMode.LIST -> {
            PostsListPaging(
                uiSettingModel = uiSettingModel,
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
            val appendFinished = (posts.loadState.append as? LoadState.NotLoading)?.endOfPaginationReached == true
            if (appendFinished && posts.itemCount == 0 && currentTag == null) {
                DefaultEmptyContent()
            }
        }
    }
}
