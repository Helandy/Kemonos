package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsState.*
import su.afk.kemonos.posts.presenter.pagePopularPosts.views.PopularPeriodsPanel
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

    val isPageLoading = posts.loadState.refresh is LoadState.Loading
    val isBusy = isPageLoading || siteSwitching

    BaseScreen(
        topBarWindowInsets = WindowInsets(0),
        topBarScroll = TopBarScroll.EnterAlways,
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
            SiteToggleFab(
                enable = !isBusy,
                selectedSite = site,
                onToggleSite = { onEvent(Event.SwitchSite) },
            )
        },
        isLoading = isPageLoading,
    ) {
        PostsContentPaging(
            postsViewMode = state.uiSettingModel.popularPostsViewMode,
            uiSettingModel = state.uiSettingModel,
            posts = posts,
            currentTag = null,
            onPostClick = { onEvent(Event.NavigateToPost(it)) },
            onRetry = { posts.retry() },
            showFavCount = true
        )
    }
}
