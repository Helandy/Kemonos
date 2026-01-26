package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.view.creator.AdaptiveCreatorsStatic
import su.afk.kemonos.common.view.creator.grid.CreatorGridItem
import su.afk.kemonos.common.view.creator.list.CreatorListItem
import su.afk.kemonos.common.view.searchBar.SearchBarNew
import su.afk.kemonos.profile.data.FreshFavoriteArtistKey
import su.afk.kemonos.profile.data.FreshFavoriteArtistsUpdates
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.favoriteProfilesSortOptions
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.uiDateBySort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteProfilesScreen(viewModel: FavoriteProfilesViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sortOptions = favoriteProfilesSortOptions()

    val pullState = rememberPullToRefreshState()
    val refreshing = state.loading

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = false,
        floatingActionButtonBottomPadding = 12.dp,
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = {
            SearchBarNew(
                query = state.searchQuery,
                onQueryChange = viewModel::updateSearch,
                services = viewModel.getServices(),
                selectedService = state.selectedService,
                onServiceSelect = viewModel::setService,
                sortOptions = sortOptions,
                selectedSort = state.sortedType,
                onSortMethodSelect = viewModel::setSortType,
                isAscending = state.sortAscending,
                onToggleAscending = viewModel::toggleSortOrder,
            )
        },
        isLoading = state.loading,
        isEmpty = state.searchCreators.isEmpty() && state.searchQuery.length >= 2,
        onRetry = viewModel::load,
    ) {
        PullToRefreshBox(
            state = pullState,
            isRefreshing = refreshing,
            onRefresh = { viewModel.load() }
        ) {
            AdaptiveCreatorsStatic(
                viewMode = state.uiSettingModel.creatorsFavoriteViewMode,
                items = state.searchCreators,
                key = { "${it.service}:${it.id}:${it.indexed}" },
                listItem = { creator ->
                    val freshSet = FreshFavoriteArtistsUpdates.get(state.selectSite)

                    val isFresh = freshSet.contains(
                        FreshFavoriteArtistKey(
                            name = creator.name,
                            service = creator.service,
                            id = creator.id
                        )
                    )

                    val dateForCard = creator.uiDateBySort(state.sortedType)

                    CreatorListItem(
                        dateMode = state.uiSettingModel.dateFormatMode,
                        service = creator.service,
                        id = creator.id,
                        name = creator.name,
                        updated = dateForCard,
                        isFresh = isFresh,
                        onClick = { viewModel.onCreatorClick(creator, isFresh) }
                    )
                },
                gridItem = { creator ->
                    val freshSet = FreshFavoriteArtistsUpdates.get(state.selectSite)
                    val isFresh = freshSet.contains(
                        FreshFavoriteArtistKey(
                            name = creator.name,
                            service = creator.service,
                            id = creator.id
                        )
                    )

                    val dateForCard = creator.uiDateBySort(state.sortedType)

                    CreatorGridItem(
                        dateMode = state.uiSettingModel.dateFormatMode,
                        service = creator.service,
                        id = creator.id,
                        name = creator.name,
                        updated = dateForCard,
                        isFresh = isFresh,
                        onClick = { viewModel.onCreatorClick(creator, isFresh) }
                    )
                }
            )
        }
    }
}
