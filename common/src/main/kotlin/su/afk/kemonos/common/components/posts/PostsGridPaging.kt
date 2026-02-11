package su.afk.kemonos.common.components.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.components.posts.postCard.PostCard
import su.afk.kemonos.common.paging.PagingAppendStateItem
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.PostsSize.Companion.toArrangement
import su.afk.kemonos.preferences.ui.PostsSize.Companion.toDp
import su.afk.kemonos.preferences.ui.UiSettingModel

@Composable
internal fun PostsGridPaging(
    uiSettingModel: UiSettingModel,
    posts: LazyPagingItems<PostDomain>,
    postClick: (PostDomain) -> Unit,
    showFavCount: Boolean,
    appendLoadState: LoadState,
    onRetryAppend: () -> Unit,
    parseError: (Throwable) -> ErrorItem,
) {
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = uiSettingModel.postsSize.toDp()),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(uiSettingModel.postsSize.toArrangement()),
        horizontalArrangement = Arrangement.spacedBy(uiSettingModel.postsSize.toArrangement())
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
                uiSettingModel = uiSettingModel,
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
