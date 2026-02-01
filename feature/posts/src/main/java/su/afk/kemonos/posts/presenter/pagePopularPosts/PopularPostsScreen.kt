package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.changeSite.SiteToggleFab
import su.afk.kemonos.common.view.posts.PostsContentPaging
import su.afk.kemonos.posts.presenter.pagePopularPosts.views.PopularPeriodsPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PopularPostsScreen(
    viewModel: PopularPostsViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val site by viewModel.site.collectAsStateWithLifecycle()
    val siteSwitching by viewModel.siteSwitching.collectAsStateWithLifecycle()

    val posts = state.posts.collectAsLazyPagingItems()

    val isPageLoading = posts.loadState.refresh is LoadState.Loading
    val isBusy = isPageLoading || siteSwitching

    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }

    BaseScreen(
        topBarWindowInsets = WindowInsets(0),
        contentWindowInsets = WindowInsets(0),
        topBarScroll = TopBarScroll.EnterAlways,
        contentPadding = PaddingValues(horizontal = 8.dp),
        isScroll = false,
        topBar = {
            PopularPeriodsPanel(
                state = state,
                onSlotClick = { period, slot ->
                    viewModel.onPeriodSlotClick(period, slot)
                }
            )
        },
        floatingActionButtonStart = {
            SiteToggleFab(
                enable = !isBusy,
                selectedSite = site,
                onToggleSite = viewModel::switchSite,
            )
        },
        floatingActionButtonBottomPadding = 12.dp,
        isLoading = isPageLoading,
    ) {
        PostsContentPaging(
            uiSettingModel = state.uiSettingModel,
            posts = posts,
            gridState = gridState,
            currentTag = null,
            onPostClick = viewModel::navigateToPost,
            onRetry = { posts.retry() },
            showFavCount = true
        )
    }
}
