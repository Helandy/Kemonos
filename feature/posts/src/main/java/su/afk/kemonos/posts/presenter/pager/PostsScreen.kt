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
import su.afk.kemonos.posts.presenter.pager.PostsPagerState.*
import su.afk.kemonos.posts.presenter.pager.model.FALLBACK_POSTS_PAGE_DESCRIPTOR
import su.afk.kemonos.posts.presenter.pager.model.POSTS_PAGES
import su.afk.kemonos.posts.presenter.pager.model.POSTS_PAGE_DESCRIPTORS
import su.afk.kemonos.posts.presenter.pager.model.indexOfOrPopular
import su.afk.kemonos.posts.presenter.pager.views.PagerTabs
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostsScreen(
    state: State,
    effect: Flow<Effect>,
    onEvent: (Event) -> Unit,
) {
    val pageDescriptors = POSTS_PAGE_DESCRIPTORS
    val pages = POSTS_PAGES
    val fallbackDescriptor = FALLBACK_POSTS_PAGE_DESCRIPTOR

    val pagerState = rememberPagerState(
        initialPage = pages.indexOfOrPopular(state.currentPage),
        pageCount = { pages.size }
    )

    // State -> Pager
    val targetIndex = pages.indexOfOrPopular(state.currentPage)
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
                val descriptor = pageDescriptors.getOrNull(settledIndex) ?: fallbackDescriptor
                onEvent(Event.SetPage(descriptor.page))
            }
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    BaseScreen(
        isScroll = false,
        topBarScroll = TopBarScroll.None,
    ) {
        PagerTabs(
            pages = pages,
            currentPage = state.currentPage,
            onTabSelected = { page ->
                onEvent(Event.SetPage(page))
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { pageIndex ->
            val descriptor = pageDescriptors.getOrNull(pageIndex) ?: fallbackDescriptor

            saveableStateHolder.SaveableStateProvider(descriptor.saveKey) {
                descriptor.content()
            }
        }
    }
}
