package su.afk.kemonos.common.presenter.screens.postsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.presenter.screens.postsScreen.postCard.PostCard
import su.afk.kemonos.domain.domain.models.PostDomain

sealed interface PostsSource<T : Any> {
    data class Paging<T : Any>(val items: LazyPagingItems<T>) : PostsSource<T>
    data class Static<T : Any>(val items: List<T>) : PostsSource<T>
}

@Composable
fun ProfilePostsGrid(
    source: PostsSource<PostDomain>,
    postClick: (PostDomain) -> Unit,
    showFavCount: Boolean = false,
) {
    val gridState = rememberLazyGridState()

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