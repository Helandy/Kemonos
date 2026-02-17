package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.posts.presenter.tagsSelect.TagsSelectState.*
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TagsPostsScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
) {
    val posts = state.posts.collectAsLazyPagingItems()

    BaseScreen(
        isScroll = false,
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        /** Контент */
        PostsContentPaging(
            postsViewMode = state.uiSettingModel.tagsPostsViewMode,
            uiSettingModel = state.uiSettingModel,
            posts = posts,
            currentTag = null,
            onPostClick = { onEvent(Event.NavigateToPost(it)) },
            onRetry = { posts.retry() },
        )
    }
}
