package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.common.R
import su.afk.kemonos.common.components.button.RandomButton
import su.afk.kemonos.common.components.button.SiteToggleFab
import su.afk.kemonos.common.components.posts.PostsContentPaging
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.presenter.pageSearchPosts.SearchPostsState.*
import su.afk.kemonos.preferences.ui.RandomButtonPlacement

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

    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }
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
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(Event.SearchQueryChanged(it)) },
                label = { Text(stringResource(R.string.search)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
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
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                )
            )
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
            uiSettingModel = state.uiSettingModel,
            posts = posts,
            currentTag = null,
            onPostClick = { onEvent(Event.NavigateToPost(it)) },
            onRetry = { posts.retry() },
            gridState = gridState
        )
    }
}
