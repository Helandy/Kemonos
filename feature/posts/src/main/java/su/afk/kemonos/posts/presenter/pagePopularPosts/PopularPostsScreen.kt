package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsState.*
import su.afk.kemonos.posts.presenter.pagePopularPosts.views.PopularPeriodsPanel
import su.afk.kemonos.preferences.ui.FabVisibilityMode
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PopularPostsScreen(
    state: State,
    effect: Flow<Effect>,
    site: SelectedSite,
    siteSwitching: Boolean,
    onEvent: (Event) -> Unit,
) {
    val posts = state.posts.collectAsLazyPagingItems()
    val pullState = rememberPullToRefreshState()

    val isPageLoading = posts.loadState.refresh is LoadState.Loading
    val isBusy = isPageLoading || siteSwitching
    val isEmptyResult = posts.itemCount == 0 && posts.loadState.refresh !is LoadState.Loading
    val topBarScrollMode = if (isEmptyResult) TopBarScroll.Pinned else TopBarScroll.EnterAlways

    BaseScreen(
        topBarWindowInsets = WindowInsets(0),
        topBarScroll = topBarScrollMode,
        contentPadding = PaddingValues(horizontal = 8.dp),
        isScroll = false,
        topBar = {
            PopularPeriodsPanel(
                state = state,
                onSlotClick = { period, slot ->
                    onEvent(Event.PeriodSlotClick(period, slot))
                }
            )
        },
        floatingActionButtonStart = {
            if (FabVisibilityMode.shouldShowSiteToggleFab(state.uiSettingModel)) {
                SiteToggleFab(
                    enable = !isBusy,
                    selectedSite = site,
                    onToggleSite = { onEvent(Event.SwitchSite) },
                )
            }
        },
        isLoading = isPageLoading && posts.itemCount == 0,
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            state = pullState,
            isRefreshing = isBusy,
            onRefresh = { onEvent(Event.PullRefresh) },
        ) {
            PostsContentPaging(
                postsViewMode = state.uiSettingModel.popularPostsViewMode,
                uiSettingModel = state.uiSettingModel,
                gridPostsSize = state.uiSettingModel.popularPostsGridSize,
                posts = posts,
                currentTag = null,
                onPostClick = { onEvent(Event.NavigateToPost(it)) },
                onRetry = { posts.retry() },
                showFavCount = true
            )
        }
    }
}
