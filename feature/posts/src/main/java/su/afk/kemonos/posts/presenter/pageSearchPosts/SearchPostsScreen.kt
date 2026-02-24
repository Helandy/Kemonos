package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.presenter.pageSearchPosts.SearchPostsState.*
import su.afk.kemonos.preferences.ui.RandomButtonPlacement
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.button.RandomButton
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.components.searchBar.PostsSearchBarWithMediaFilters
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchPostsScreen(
    state: State,
    effect: Flow<Effect>,
    site: SelectedSite,
    siteSwitching: Boolean,
    onEvent: (Event) -> Unit,
) {
    val posts = state.posts.collectAsLazyPagingItems()

    val isPageLoading = posts.loadState.refresh is LoadState.Loading
    val isBusy = isPageLoading || siteSwitching

    val focusManager = LocalFocusManager.current

    val placement = state.uiSettingModel.randomButtonPlacement
    val showRandomInSearchBar = placement == RandomButtonPlacement.SEARCH_BAR
    val showRandomFab = placement == RandomButtonPlacement.SCREEN

    BaseScreen(
        topBarWindowInsets = WindowInsets(0),
        topBarScroll = TopBarScroll.EnterAlways,
        contentPadding = PaddingValues(horizontal = 8.dp),
        isScroll = false,
        topBar = {
            PostsSearchBarWithMediaFilters(
                query = state.searchQuery,
                onQueryChange = { onEvent(Event.SearchQueryChanged(it)) },
                mediaFilter = state.mediaFilter,
                onToggleHasVideo = { onEvent(Event.ToggleHasVideo) },
                onToggleHasAttachments = { onEvent(Event.ToggleHasAttachments) },
                onToggleHasImages = { onEvent(Event.ToggleHasImages) },
                label = stringResource(R.string.search),
                showMediaFiltersInfoTooltip = true,
                trailingIcon = {
                    if (showRandomInSearchBar) {
                        IconButton(
                            onClick = { onEvent(Event.RandomPost) },
                            enabled = !isBusy,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Casino,
                                contentDescription = stringResource(R.string.random),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                onSearch = {
                    focusManager.clearFocus()
                    onEvent(Event.SearchSubmitted)
                },
                bottomPadding = 0
            )
            if (state.recentSearches.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(
                        items = state.recentSearches,
                        key = { index, query -> "$query-$index" }
                    ) { _, query ->
                        InputChip(
                            selected = false,
                            onClick = {
                                focusManager.clearFocus()
                                onEvent(Event.RecentSearchSelected(query))
                            },
                            label = {
                                Text(
                                    text = query,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 180.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Remove search",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onEvent(Event.RemoveRecentSearch(query)) }
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButtonStart = {
            SiteToggleFab(
                enable = !isBusy,
                selectedSite = site,
                onToggleSite = { onEvent(Event.SwitchSite) },
            )
        },
        floatingActionButtonEnd = {
            if (showRandomFab) {
                RandomButton(
                    enabled = !isBusy,
                    onClick = { onEvent(Event.RandomPost) }
                )
            }
        },
    ) {
        /** Контент */
        PostsContentPaging(
            postsViewMode = state.uiSettingModel.searchPostsViewMode,
            uiSettingModel = state.uiSettingModel,
            posts = posts,
            currentTag = null,
            onPostClick = { onEvent(Event.NavigateToPost(it)) },
            onRetry = { posts.retry() },
        )
    }
}
