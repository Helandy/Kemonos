package su.afk.kemonos.ui.components.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.PostDomain.Companion.stableKey
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.components.posts.postCard.PostCard
import su.afk.kemonos.ui.paging.PagingAppendStateItem

@Composable
internal fun PostsListPaging(
    uiSettingModel: UiSettingModel,
    posts: LazyPagingItems<PostDomain>,
    onPostClick: (PostDomain) -> Unit,
    showFavCount: Boolean,
    appendLoadState: LoadState,
    onRetryAppend: () -> Unit,
    header: (@Composable () -> Unit)? = null,
    parseError: (Throwable) -> ErrorItem,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 72.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (header != null) {
            item {
                header()
            }
        }

        items(
            count = posts.itemCount,
            key = { index -> posts.peek(index)?.stableKey() ?: "placeholder_$index" }
        ) { index ->
            val post = posts[index] ?: return@items
            PostCard(
                post = post,
                onClick = { onPostClick(post) },
                showFavCount = showFavCount,
                uiSettingModel = uiSettingModel
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
