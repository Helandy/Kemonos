package su.afk.kemonos.posts.presenter.pager

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsNavigation
import su.afk.kemonos.posts.presenter.pageSearchPosts.SearchPostsNavigation
import su.afk.kemonos.posts.presenter.pageTags.TagsPageNavigation
import su.afk.kemonos.posts.presenter.pager.PostsPagerState.*
import su.afk.kemonos.posts.presenter.pager.model.ALL_POSTS_PAGES
import su.afk.kemonos.posts.presenter.pager.model.PostsPage
import su.afk.kemonos.posts.presenter.pager.views.PagerTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostsScreen(
    state: State,
    effect: Flow<Effect>,
    onEvent: (Event) -> Unit,
) {
    val pages = ALL_POSTS_PAGES

    val pagerState = rememberPagerState(
        initialPage = pages.indexOf(state.currentPage).coerceAtLeast(0),
        pageCount = { pages.size }
    )

    // State -> Pager
    val targetIndex = pages.indexOf(state.currentPage).coerceAtLeast(0)
    LaunchedEffect(targetIndex) {
        if (pagerState.currentPage != targetIndex) {
            pagerState.animateScrollToPage(targetIndex)
        }
    }

    // Pager -> State
    LaunchedEffect(pagerState, pages) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { settledIndex ->
                val page = pages.getOrNull(settledIndex) ?: PostsPage.Popular
                onEvent(Event.SetPage(page))
            }
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    BaseScreen(
        isScroll = false,
        topBarScroll = TopBarScroll.None,
    ) {
        PagerTabs(
            currentPage = state.currentPage,
            onTabSelected = { page ->
                onEvent(Event.SetPage(page))
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { pageIndex ->
            val page = pages.getOrNull(pageIndex) ?: PostsPage.Popular

            val key = when (page) {
                PostsPage.Search -> "posts_page_search"
                PostsPage.Popular -> "posts_page_popular"
                PostsPage.Tags -> "posts_page_tags"
            }

            saveableStateHolder.SaveableStateProvider(key) {
                when (page) {
                    PostsPage.Search -> SearchPostsNavigation()
                    PostsPage.Popular -> PopularPostsNavigation()
                    PostsPage.Tags -> TagsPageNavigation()
                }
            }
        }
    }
}
