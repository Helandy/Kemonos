package su.afk.kemonos.creators.presenter

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.creators.presenter.CreatorsState.*
import su.afk.kemonos.creators.presenter.model.creatorsSortOptions
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.RandomButtonPlacement
import su.afk.kemonos.ui.components.button.RandomButton
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.components.creator.CreatorsContentPaging
import su.afk.kemonos.ui.components.searchBar.SearchBarNew
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

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
    val isBusy = state.loading || siteSwitching

    val showEmpty = state.searchQuery.trim().length >= 2 &&
            pagingItems.loadState.refresh is LoadState.NotLoading &&
            pagingItems.itemCount == 0

    val refreshState = pagingItems.loadState.refresh
    val isFirstPageLoading = refreshState is LoadState.Loading && pagingItems.itemCount == 0

    val isScreenLoading = isBusy || isFirstPageLoading

    val showRandomInSearch = state.uiSettingModel.randomButtonPlacement == RandomButtonPlacement.SEARCH_BAR
    val showRandomFab = state.uiSettingModel.randomButtonPlacement == RandomButtonPlacement.SCREEN

    BaseScreen(
        isScroll = false,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = {
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
                RandomButton(
                    enabled = !isBusy,
                    onClick = { onEvent(Event.RandomClicked) },
                )
            }
        },
        isLoading = isScreenLoading,
        isEmpty = showEmpty
    ) {
        CreatorsContentPaging(
            dateMode = state.uiSettingModel.dateFormatMode,
            viewMode = state.uiSettingModel.creatorsViewMode,
            pagingItems = pagingItems,
            randomItems =
                if (state.uiSettingModel.suggestRandomAuthors)
                    state.randomSuggestionsFiltered
                else
                    emptyList(),
            onCreatorClick = { onEvent(Event.CreatorClicked(it)) },
            expanded = state.randomExpanded,
            onClickRandomHeader = { onEvent(Event.ToggleRandomExpanded) },
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