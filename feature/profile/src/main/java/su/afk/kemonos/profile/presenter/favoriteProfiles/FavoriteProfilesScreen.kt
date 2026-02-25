package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FreshFavoriteArtistKey
import su.afk.kemonos.profile.presenter.favoriteProfiles.FavoriteProfilesState.*
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.favoriteProfilesSortOptions
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.uiDateBySort
import su.afk.kemonos.ui.components.creator.CreatorsContentPaging
import su.afk.kemonos.ui.components.searchBar.SearchBarNew
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.DefaultEmptyContent
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteProfilesScreen(state: State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val sortOptions = favoriteProfilesSortOptions()
    val pullState = rememberPullToRefreshState()
    val pagingItems = state.artistsPaged.collectAsLazyPagingItems()
    val pagingIsEmpty = pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0

    BaseScreen(
        contentPadding = PaddingValues(horizontal = 8.dp),
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.favorite_profiles_title),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
        isLoading = state.loading,
        onRetry = { onEvent(Event.Retry) },
    ) {
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

        if (pagingIsEmpty) {
            DefaultEmptyContent()
        }

        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            state = pullState,
            isRefreshing = state.refreshing,
            onRefresh = {
                onEvent(Event.Refresh)
                pagingItems.refresh()
            }
        ) {
            CreatorsContentPaging(
                dateMode = state.uiSettingModel.dateFormatMode,
                viewMode = state.uiSettingModel.creatorsFavoriteViewMode,
                pagingItems = pagingItems,
                randomItems = emptyList(),
                onCreatorClick = { creator ->
                    onEvent(Event.CreatorClicked(creator = creator, isFresh = false))
                },
                updatedProvider = { artist ->
                    artist.uiDateBySort(state.sortedType)
                },
                isFreshProvider = { artist ->
                    state.freshSet.contains(
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
