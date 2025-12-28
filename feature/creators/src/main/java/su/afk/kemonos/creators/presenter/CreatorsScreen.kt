package su.afk.kemonos.creators.presenter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.changeSite.SiteToggleFab
import su.afk.kemonos.common.presenter.views.creator.CreatorItem
import su.afk.kemonos.common.presenter.views.searchBar.SearchBarNew
import su.afk.kemonos.creators.presenter.views.creatorsSortOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorsScreen(viewModel: CreatorsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sortOptions = creatorsSortOptions()

    val site by viewModel.site.collectAsStateWithLifecycle()
    val siteSwitching by viewModel.siteSwitching.collectAsStateWithLifecycle()

    val pagingItems = state.creatorsPaged.collectAsLazyPagingItems()
    val isBusy = state.refreshing || siteSwitching

    val refreshState = pagingItems.loadState.refresh
    val isPagingRefreshing = refreshState is LoadState.Loading
    val isFirstPageLoading = isPagingRefreshing && pagingItems.itemCount == 0

    val showEmpty = state.searchQuery.trim().length >= 2 &&
            pagingItems.loadState.refresh is LoadState.NotLoading &&
            pagingItems.itemCount == 0

    BaseScreen(
        isScroll = false,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        floatingActionButtonBottomPadding = 12.dp,
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = { scrollBehavior ->
            StandardTopBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
            ) {
                SearchBarNew(
                    query = state.searchQuery,
                    onQueryChange = viewModel::updateSearch,
                    services = state.services,
                    selectedService = state.selectedService,
                    onServiceSelect = viewModel::setService,
                    selectedSort = state.sortedType,
                    sortOptions = sortOptions,
                    onSortMethodSelect = viewModel::setSortType,
                    isAscending = state.sortAscending,
                    onToggleAscending = viewModel::toggleSortOrder,
                    showRandom = true,
                    onRandomClick = viewModel::randomCreator,
                )
            }
        },
        floatingActionButtonStart = {
            SiteToggleFab(
                enable = !isBusy,
                selectedSite = site,
                onToggleSite = viewModel::switchSite,
            )
        },
        isLoading = isBusy || isFirstPageLoading || state.loading,
        isEmpty = showEmpty
    ) {
        LazyColumn {
            item {
                HorizontalDivider(
                    Modifier.padding(top = 4.dp),
                    DividerDefaults.Thickness,
                    DividerDefaults.color
                )
            }

            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { "${it.service}:${it.id}:${it.indexed}" }
            ) { index ->
                val creator = pagingItems[index] ?: return@items
                CreatorItem(
                    service = creator.service,
                    id = creator.id,
                    name = creator.name,
                    favorited = creator.favorited,
                    onClick = { viewModel.onCreatorClick(creator) }
                )
                HorizontalDivider()
            }
        }
    }
}