package su.afk.kemonos.common.view.posts.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.view.posts.paging.PagingAppendStateItem
import su.afk.kemonos.common.view.posts.postCard.PostCard
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
fun PostsListPaging(
    dateMode: DateFormatMode,
    posts: LazyPagingItems<PostDomain>,
    onPostClick: (PostDomain) -> Unit,
    showFavCount: Boolean,
    appendLoadState: LoadState,
    onRetryAppend: () -> Unit,
    parseError: (Throwable) -> ErrorItem,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = posts.itemCount,
            key = { index -> posts.peek(index)?.id ?: "placeholder_$index" }
        ) { index ->
            val post = posts[index] ?: return@items
            PostCard(
                post = post,
                onClick = { onPostClick(post) },
                showFavCount = showFavCount,
                dateMode = dateMode
            )
        }

        /** Loading + error retry button */
        item {
            PagingAppendStateItem(
                loadState = appendLoadState,
                onRetry = onRetryAppend,
                parseError = parseError
            )
        }
    }
}