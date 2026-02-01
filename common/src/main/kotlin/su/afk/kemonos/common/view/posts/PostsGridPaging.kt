package su.afk.kemonos.common.view.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.paging.PagingAppendStateItem
import su.afk.kemonos.common.view.posts.postCard.PostCard
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.preferences.ui.toDp

@Composable
internal fun PostsGridPaging(
    uiSettingModel: UiSettingModel,
    posts: LazyPagingItems<PostDomain>,
    postClick: (PostDomain) -> Unit,
    showFavCount: Boolean = false,
    gridState: LazyGridState,
    appendLoadState: LoadState,
    onRetryAppend: () -> Unit,
    parseError: (Throwable) -> ErrorItem,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = uiSettingModel.postsSize.toDp()),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = posts.itemCount,
            key = { index -> posts.peek(index)?.id ?: "placeholder_$index" }
        ) { index ->
            val post = posts[index] ?: return@items
            PostCard(
                post = post,
                onClick = { postClick(post) },
                showFavCount = showFavCount,
                dateMode = uiSettingModel.dateFormatMode,
                blurImage = uiSettingModel.blurImages
            )
        }

        /** Loading + error retry button */
        item(span = { GridItemSpan(maxLineSpan) }) {
            PagingAppendStateItem(
                loadState = appendLoadState,
                onRetry = onRetryAppend,
                parseError = parseError
            )
        }
    }
}