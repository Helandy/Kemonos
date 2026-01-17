package su.afk.kemonos.creators.presenter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.changeSite.SiteToggleFab
import su.afk.kemonos.common.presenter.views.RandomFab
import su.afk.kemonos.common.presenter.views.searchBar.SearchBarNew
import su.afk.kemonos.creators.presenter.model.creatorsSortOptions
import su.afk.kemonos.creators.presenter.views.CreatorsContentPaging
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.preferences.ui.RandomButtonPlacement

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

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    // ключ, который меняется, когда random секция появляется/исчезает
    val randomVisible = state.randomSuggestions.isNotEmpty()

    LaunchedEffect(
        state.uiSettingModel.creatorsViewMode,
        randomVisible,
        state.searchQuery.trim(),
        state.selectedService,
        state.sortedType,
        state.sortAscending
    ) {
        when (state.uiSettingModel.creatorsViewMode) {
            CreatorViewMode.LIST -> listState.scrollToItem(0)
            CreatorViewMode.GRID -> gridState.scrollToItem(0)
        }
    }

    val showRandomInSearch = state.uiSettingModel.randomButtonPlacement == RandomButtonPlacement.SEARCH_BAR
    val showRandomFab = state.uiSettingModel.randomButtonPlacement == RandomButtonPlacement.SCREEN

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
                    showRandom = showRandomInSearch && !isBusy,
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

        floatingActionButtonEnd = {
            if (showRandomFab) {
                RandomFab(
                    enabled = !isBusy,
                    onClick = viewModel::randomCreator,
                )
            }
        },
        isLoading = isBusy || isFirstPageLoading || state.loading,
        isEmpty = showEmpty
    ) {
        val viewMode = state.uiSettingModel.creatorsViewMode

        CreatorsContentPaging(
            viewMode = viewMode,
            pagingItems = pagingItems,
            randomItems = if (state.searchQuery.trim().isEmpty()) state.randomSuggestions else emptyList(),
            onCreatorClick = viewModel::onCreatorClick,
            listState = listState,
            gridState = gridState,
        )
    }
}