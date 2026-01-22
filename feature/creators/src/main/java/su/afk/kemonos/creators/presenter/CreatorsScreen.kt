package su.afk.kemonos.creators.presenter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.changeSite.SiteToggleFab
import su.afk.kemonos.common.presenter.views.RandomFab
import su.afk.kemonos.common.presenter.views.searchBar.SearchBarNew
import su.afk.kemonos.creators.presenter.CreatorsState.*
import su.afk.kemonos.creators.presenter.model.creatorsSortOptions
import su.afk.kemonos.creators.presenter.views.CreatorsContentPaging
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.preferences.ui.RandomButtonPlacement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorsScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
    site: SelectedSite,
    siteSwitching: Boolean,
) {
    val sortOptions = creatorsSortOptions()

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

    val viewModeState by rememberUpdatedState(state.uiSettingModel.creatorsViewMode)
    LaunchedEffect(effect) {
        effect.collect { e ->
            when (e) {
                Effect.ScrollToTop -> {
                    when (viewModeState) {
                        CreatorViewMode.LIST -> listState.scrollToItem(0)
                        CreatorViewMode.GRID -> gridState.scrollToItem(0)
                    }
                }
            }
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
                    onQueryChange = { onEvent(Event.QueryChanged(it)) },
                    services = state.services,
                    selectedService = state.selectedService,
                    onServiceSelect = { onEvent(Event.ServiceSelected(it)) },
                    selectedSort = state.sortedType,
                    sortOptions = sortOptions,
                    onSortMethodSelect = { onEvent(Event.SortSelected(it)) },
                    isAscending = state.sortAscending,
                    onToggleAscending = { onEvent(Event.ToggleSortOrder) },
                    showRandom = showRandomInSearch && !isBusy,
                    onRandomClick = { onEvent(Event.RandomClicked) },
                )
            }
        },
        floatingActionButtonStart = {
            SiteToggleFab(
                enable = !isBusy,
                selectedSite = site,
                onToggleSite = { onEvent(Event.SwitchSiteClicked) },
            )
        },

        floatingActionButtonEnd = {
            if (showRandomFab) {
                RandomFab(
                    enabled = !isBusy,
                    onClick = { onEvent(Event.RandomClicked) },
                )
            }
        },
        isLoading = isBusy || isFirstPageLoading || state.loading,
        isEmpty = showEmpty
    ) {
        CreatorsContentPaging(
            dateMode = state.uiSettingModel.dateFormatMode,
            viewMode = state.uiSettingModel.creatorsViewMode,
            pagingItems = pagingItems,
            randomItems = if (state.searchQuery.trim().isEmpty()) state.randomSuggestions else emptyList(),
            onCreatorClick = { onEvent(Event.CreatorClicked(it)) },
            listState = listState,
            gridState = gridState,
        )
    }
}

@Preview("PreviewCreatorsScreen")
@Composable
private fun PreviewCreatorsScreen() {
    CreatorsScreen(
        state = State(),
        onEvent = {},
        effect = emptyFlow(),
        site = SelectedSite.K,
        siteSwitching = false,
    )
}