package su.afk.kemonos.posts.presenter.pager.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.posts.presenter.pager.model.ALL_POSTS_PAGES
import su.afk.kemonos.posts.presenter.pager.model.PostsPage

@Composable
internal fun PagerTabs(
    currentPage: PostsPage,
    onTabSelected: (PostsPage) -> Unit,
) {
    val pages = remember { ALL_POSTS_PAGES }

    val selectedIndex = pages.indexOf(currentPage).let { idx ->
        if (idx >= 0) idx else 0
    }
    val safeCurrentPage = pages.getOrNull(selectedIndex) ?: PostsPage.Search

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 8.dp,
    ) {
        pages.forEach { page ->
            Tab(
                selected = safeCurrentPage == page,
                onClick = { onTabSelected(page) },
                text = {
                    Text(
                        text = stringResource(page.titleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                    )
                }
            )
        }
    }
}