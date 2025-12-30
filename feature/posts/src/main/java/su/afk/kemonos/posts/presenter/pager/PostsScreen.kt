package su.afk.kemonos.posts.presenter.pager

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsNavigation
import su.afk.kemonos.posts.presenter.pageSearchPosts.SearchPostsNavigation
import su.afk.kemonos.posts.presenter.pageTags.TagsPageNavigation
import su.afk.kemonos.posts.presenter.pager.model.ALL_POSTS_PAGES
import su.afk.kemonos.posts.presenter.pager.model.PostsPage
import su.afk.kemonos.posts.presenter.pager.views.PagerTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostsScreen(
    viewModel: PostsPagerViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pages: List<PostsPage> = remember { ALL_POSTS_PAGES }

    val initialIndex = pages.indexOf(state.currentPage).let { idx ->
        if (idx >= 0) idx else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { pages.size }
    )

    LaunchedEffect(state.currentPage) {
        val targetIndex = pages.indexOf(state.currentPage).let { idx ->
            if (idx >= 0) idx else 0
        }
        if (pagerState.currentPage != targetIndex) {
            pagerState.animateScrollToPage(targetIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { pageIndex ->
                val page = pages.getOrNull(pageIndex) ?: PostsPage.Search
                viewModel.onPageChanged(page)
            }
    }
    val saveableStateHolder = rememberSaveableStateHolder()

    BaseScreen(
        isScroll = false,
    ) {
        PagerTabs(
            currentPage = state.currentPage,
            onTabSelected = { page ->
                viewModel.onTabClick(page)
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { pageIndex ->
            val page = pages.getOrNull(pageIndex) ?: PostsPage.Search

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