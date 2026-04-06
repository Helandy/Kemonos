package su.afk.kemonos.creators.presenter

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import su.afk.kemonos.creators.presenter.CreatorsState.*
import su.afk.kemonos.creators.presenter.model.creatorsSortOptions
import su.afk.kemonos.creators.presenter.view.CreatorsGithubRateBanner
import su.afk.kemonos.creators.presenter.view.CreatorsVideoInfoDomainBanner
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.preferences.ui.FabVisibilityMode
import su.afk.kemonos.preferences.ui.RandomButtonPlacement
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.components.button.RandomButton
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.components.creator.CreatorsContentPaging
import su.afk.kemonos.ui.components.searchBar.SearchBarNew
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorsScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
    site: SelectedSite,
    siteSwitching: Boolean,
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    val sortOptions = creatorsSortOptions()

    val pagingItems = state.creatorsPaged.collectAsLazyPagingItems()
    val isEmptyResult = state.searchQuery.trim().length >= 2 &&
            pagingItems.loadState.refresh is LoadState.NotLoading &&
            pagingItems.itemCount == 0

    val refreshState = pagingItems.loadState.refresh
    val isFirstPageLoading = !isPreview && refreshState is LoadState.Loading && pagingItems.itemCount == 0

    val visibleRandomItems =
        if (state.uiSettingModel.suggestRandomAuthors) state.randomSuggestionsFiltered else emptyList()
    val isScreenLoading = state.loading || siteSwitching || isFirstPageLoading

    val showRandomInSearch = state.uiSettingModel.randomButtonPlacement == RandomButtonPlacement.SEARCH_BAR
    val showRandomFab = state.uiSettingModel.randomButtonPlacement == RandomButtonPlacement.SCREEN
    val topBarScrollMode = if (isEmptyResult) TopBarScroll.Pinned else TopBarScroll.EnterAlways


    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    LaunchedEffect(Unit) {
        effect.collect { item ->
            when (item) {
                Effect.ScrollToTop -> when (state.uiSettingModel.creatorsViewMode) {
                    CreatorViewMode.LIST -> listState.scrollToItem(0)
                    CreatorViewMode.GRID -> gridState.scrollToItem(0)
                }

                is Effect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, item.url.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }

    BaseScreen(
        isScroll = false,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = topBarScrollMode,
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
                showRandom = showRandomInSearch,
                randomEnabled = !isScreenLoading,
                onRandomClick = { onEvent(Event.RandomClicked) },
            )
        },
        floatingActionButtonStart = {
            val showFab = when (state.uiSettingModel.fabVisibilityMode) {
                FabVisibilityMode.ALWAYS_ON -> true
                FabVisibilityMode.ALWAYS_OFF -> false
                FabVisibilityMode.ON_BOTH -> state.uiSettingModel.showKemono && state.uiSettingModel.showCoomer
            }
            if (showFab) {
                SiteToggleFab(
                    enable = !isScreenLoading,
                    selectedSite = site,
                    onToggleSite = { onEvent(Event.SwitchSiteClicked) },
                )
            }
        },
        floatingActionButtonEnd = {
            if (showRandomFab) {
                RandomButton(
                    enabled = !isScreenLoading,
                    onClick = { onEvent(Event.RandomClicked) },
                )
            }
        },
        isLoading = isScreenLoading,
        isEmpty = isEmptyResult
    ) {
        CreatorsContentPaging(
            dateMode = state.uiSettingModel.dateFormatMode,
            viewMode = state.uiSettingModel.creatorsViewMode,
            pagingItems = pagingItems,
            randomItems = visibleRandomItems,
            topContent = if (state.showGithubRateBanner || (state.showVideoInfoDomainBanner && state.isVideoInfoDomainAvailable == false)) {
                {
                    if (state.showVideoInfoDomainBanner && state.isVideoInfoDomainAvailable == false) {
                        CreatorsVideoInfoDomainBanner(
                            onClose = { onEvent(Event.HideVideoInfoDomainBanner) },
                        )
                    }

                    CreatorsGithubRateBanner(
                        visible = state.showGithubRateBanner,
                        onRateClick = { onEvent(Event.GithubRateClick) },
                        onNeverShowClick = { onEvent(Event.HideGithubRateBanner) },
                    )
                }
            } else {
                null
            },
            onCreatorClick = { onEvent(Event.CreatorClicked(it)) },
            expanded = state.randomExpanded,
            onClickRandomHeader = { onEvent(Event.HeaderRandomExpanded) },
            listState = listState,
            gridState = gridState,
        )
    }
}

@Preview(name = "Creators Loading", showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun PreviewCreatorsScreenLoading() {
    KemonosPreviewScreen {
        CreatorsScreen(
            state = previewState(loading = true),
            onEvent = {},
            effect = emptyFlow(),
            site = SelectedSite.K,
            siteSwitching = false,
        )
    }
}

@Preview(name = "Creators Empty Search", showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun PreviewCreatorsScreenEmpty() {
    KemonosPreviewScreen {
        CreatorsScreen(
            state = previewState(
                loading = false,
                query = "zzzz",
                creators = emptyList(),
                randomExpanded = false,
                suggestRandomAuthors = false,
            ),
            onEvent = {},
            effect = emptyFlow(),
            site = SelectedSite.K,
            siteSwitching = false,
        )
    }
}

@Preview(name = "Creators List + Random", showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun PreviewCreatorsScreenListWithRandom() {
    KemonosPreviewScreen {
        CreatorsScreen(
            state = previewState(
                loading = false,
                creators = sampleCreators(24),
                random = sampleCreators(6, prefix = "Random"),
                randomExpanded = true,
                suggestRandomAuthors = true,
                viewMode = CreatorViewMode.LIST,
            ),
            onEvent = {},
            effect = emptyFlow(),
            site = SelectedSite.K,
            siteSwitching = false,
        )
    }
}

@Preview(name = "Creators Grid + Random", showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun PreviewCreatorsScreenGridWithRandom() {
    KemonosPreviewScreen {
        CreatorsScreen(
            state = previewState(
                loading = false,
                creators = sampleCreators(30),
                random = sampleCreators(8, prefix = "Random"),
                randomExpanded = true,
                suggestRandomAuthors = true,
                viewMode = CreatorViewMode.GRID,
            ),
            onEvent = {},
            effect = emptyFlow(),
            site = SelectedSite.C,
            siteSwitching = false,
        )
    }
}

private fun previewState(
    loading: Boolean,
    query: String = "",
    creators: List<FavoriteArtist> = sampleCreators(20),
    random: List<FavoriteArtist> = emptyList(),
    randomExpanded: Boolean = true,
    suggestRandomAuthors: Boolean = false,
    viewMode: CreatorViewMode = CreatorViewMode.LIST,
): State = State(
    loading = loading,
    searchQuery = query,
    creatorsPaged = flowOf(PagingData.from(creators)),
    randomSuggestions = random,
    randomSuggestionsFiltered = random,
    randomExpanded = randomExpanded,
    uiSettingModel = UiSettingModel(
        creatorsViewMode = viewMode,
        suggestRandomAuthors = suggestRandomAuthors,
        randomButtonPlacement = RandomButtonPlacement.SEARCH_BAR,
    ),
)

private fun sampleCreators(count: Int, prefix: String = "Creator"): List<FavoriteArtist> =
    List(count) { index ->
        FavoriteArtist(
            favedSeq = index,
            id = "id_$index",
            indexed = "2026-01-${(index % 28) + 1}",
            lastImported = "2026-01-${(index % 28) + 1}",
            name = "$prefix $index",
            publicId = "public_$index",
            relationId = index,
            service = if (index % 2 == 0) "patreon" else "fanbox",
            updated = "2026-02-${(index % 28) + 1}",
            favorited = 1000 - index,
        )
    }
