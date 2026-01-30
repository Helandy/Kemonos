package su.afk.kemonos.common.view.posts.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import su.afk.kemonos.common.view.posts.grid.PostsSource
import su.afk.kemonos.common.view.posts.postCard.PostCard

import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
fun PostsList(
    dateMode: DateFormatMode,
    source: PostsSource<PostDomain>,
    onPostClick: (PostDomain) -> Unit,
    showFavCount: Boolean = false,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (source) {
            is PostsSource.Paging -> {
                val posts: LazyPagingItems<PostDomain> = source.items
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
                        onClick = { onPostClick(post) },
                        showFavCount = showFavCount,
                        dateMode = dateMode
                    )
                }
            }
        }
    }
}