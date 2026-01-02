package su.afk.kemonos.common.presenter.screens.postsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.presenter.screens.postsScreen.paging.PagingAppendStateItem
import su.afk.kemonos.common.presenter.screens.postsScreen.postCard.PostCard
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.domain.models.PostDomain

sealed interface PostsSource<T : Any> {
    data class Paging<T : Any>(val items: LazyPagingItems<T>) : PostsSource<T>
    data class Static<T : Any>(val items: List<T>) : PostsSource<T>
}

@Composable
fun ProfilePostsGrid(
    source: PostsSource<PostDomain>,
    postClick: (PostDomain) -> Unit,
    showFavCount: Boolean = false,
    gridState: LazyGridState,
    appendLoadState: LoadState? = null,
    onRetryAppend: (() -> Unit)? = null,
    parseError: ((Throwable) -> ErrorItem)? = null,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (source) {
            is PostsSource.Paging -> {
                val posts = source.items
                items(
                    count = posts.itemCount,
                    key = { index -> posts.peek(index)?.id ?: "placeholder_$index" }
                ) { index ->
                    val post = posts[index] ?: return@items
                    PostCard(
                        post = post,
                        onClick = { postClick(post) },
                        showFavCount = showFavCount,
                    )
                }

                /** Loading + error retry buuton */
                if (appendLoadState != null && onRetryAppend != null && parseError != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PagingAppendStateItem(
                            loadState = appendLoadState,
                            onRetry = onRetryAppend,
                            parseError = parseError
                        )
                    }
                }
            }

            is PostsSource.Static -> {
                val list = source.items
                items(
                    count = list.size,
                    key = { index -> list[index].id }
                ) { index ->
                    val post = list[index]
                    PostCard(
                        post = post,
                        onClick = { postClick(post) },
                        showFavCount = showFavCount,
                    )
                }
            }
        }
    }
}