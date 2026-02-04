package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.common.view.creator.CreatorsContentPaging
import su.afk.kemonos.common.view.searchBar.SearchBarNew
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.profile.data.FreshFavoriteArtistKey
import su.afk.kemonos.profile.data.FreshFavoriteArtistsUpdates
import su.afk.kemonos.profile.presenter.favoriteProfiles.FavoriteProfilesState.*
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.favoriteProfilesSortOptions
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.uiDateBySort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteProfilesScreen(state: State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val sortOptions = favoriteProfilesSortOptions()
    val pullState = rememberPullToRefreshState()
    val pagingItems = state.artistsPaged.collectAsLazyPagingItems()

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    LaunchedEffect(effect) {
        effect.collect { e ->
            when (e) {
                Effect.ScrollToTop -> {
                    when (state.uiSettingModel.creatorsViewMode) {
                        CreatorViewMode.LIST -> listState.scrollToItem(0)
                        CreatorViewMode.GRID -> gridState.scrollToItem(0)
                    }
                }
            }
        }
    }

    BaseScreen(
        contentPadding = PaddingValues(horizontal = 8.dp),
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = {
            SearchBarNew(
                query = state.searchQuery,
                onQueryChange = { onEvent(Event.QueryChanged(it)) },

                services = state.services,
                selectedService = state.selectedService,
                onServiceSelect = { onEvent(Event.ServiceSelected(it)) },

                sortOptions = sortOptions,
                selectedSort = state.sortedType,
                onSortMethodSelect = { onEvent(Event.SortSelected(it)) },

                isAscending = state.sortAscending,
                onToggleAscending = { onEvent(Event.ToggleSortOrder) },
            )
        },
        isLoading = state.loading,
        onRetry = { onEvent(Event.Retry) },
    ) {
        PullToRefreshBox(
            state = pullState,
            isRefreshing = state.refreshing,
            onRefresh = {
                onEvent(Event.Refresh)
                pagingItems.refresh()
            }
        ) {
            CreatorsContentPaging(
                dateMode = state.uiSettingModel.dateFormatMode,
                viewMode = state.uiSettingModel.creatorsViewMode,
                pagingItems = pagingItems,
                randomItems = emptyList(),
                onCreatorClick = { creator ->
                    onEvent(Event.CreatorClicked(creator = creator, isFresh = false))
                },
                listState = listState,
                gridState = gridState,
                updatedProvider = { artist ->
                    artist.uiDateBySort(state.sortedType)
                },
                isFreshProvider = { artist ->
                    val freshSet = FreshFavoriteArtistsUpdates.get(state.selectedSite)
                    freshSet.contains(
                        FreshFavoriteArtistKey(
                            name = artist.name,
                            service = artist.service,
                            id = artist.id
                        )
                    )
                },
            )
        }
    }
}

@Preview("PreviewFavoriteProfilesScreen")
@Composable
private fun PreviewFavoriteProfilesScreen() {
    KemonosPreviewScreen {
        FavoriteProfilesScreen(
            state = State(),
            onEvent = {},
            effect = emptyFlow(),
        )
    }
}