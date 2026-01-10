package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.views.creator.CreatorItem
import su.afk.kemonos.common.presenter.views.searchBar.SearchBarNew
import su.afk.kemonos.profile.data.FreshFavoriteArtistKey
import su.afk.kemonos.profile.data.FreshFavoriteArtistsUpdates
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.favoriteProfilesSortOptions
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.uiDateBySort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteProfilesScreen(viewModel: FavoriteProfilesViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sortOptions = favoriteProfilesSortOptions()

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = false,
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
                    services = viewModel.getServices(),
                    selectedService = state.selectedService,
                    onServiceSelect = viewModel::setService,
                    sortOptions = sortOptions,
                    selectedSort = state.sortedType,
                    onSortMethodSelect = viewModel::setSortType,
                    isAscending = state.sortAscending,
                    onToggleAscending = viewModel::toggleSortOrder,
                )
            }
        },
        isLoading = state.loading,
        isEmpty = state.searchCreators.isEmpty() && state.searchQuery.length >= 2,
        onRetry = { viewModel.load() }
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
                items = state.searchCreators,
                key = { "${it.service}:${it.id}:${it.indexed}" }
            ) { creator ->
                val freshSet = FreshFavoriteArtistsUpdates.get(state.selectSite)

                val isFresh = freshSet.contains(
                    FreshFavoriteArtistKey(
                        name = creator.name,
                        service = creator.service,
                        id = creator.id
                    )
                )

                val dateForCard = creator.uiDateBySort(state.sortedType)

                CreatorItem(
                    service = creator.service,
                    id = creator.id,
                    name = creator.name,
                    updated = dateForCard,
                    isFresh = isFresh,
                    onClick = { viewModel.onCreatorClick(creator, isFresh) }
                )
                HorizontalDivider()
            }
        }
    }
}
