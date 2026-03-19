package su.afk.kemonos.posts.presenter.pageTags

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.posts.presenter.pageTags.TagsPageState.*
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TagsPageScreen(
    state: State,
    effect: Flow<Effect>,
    site: SelectedSite,
    siteSwitching: Boolean,
    onEvent: (Event) -> Unit,
) {
    val isPageLoading = state.loading || siteSwitching
    val hasActiveSearch = state.searchQuery.trim().length >= 2
    val isEmptyResult = hasActiveSearch && state.filteredTags.isEmpty()
    val pullState = rememberPullToRefreshState()

    val chunkedTags = remember(state.filteredTags) {
        state.filteredTags.chunked(50)
    }
    val focusManager = LocalFocusManager.current

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
                    if (state.searchQuery.isNotBlank()) {
                        IconButton(onClick = { onEvent(Event.SearchQueryChanged("")) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                )
            )
        },
        floatingActionButtonStart = {
            SiteToggleFab(
                enable = !isPageLoading,
                selectedSite = site,
                onToggleSite = { onEvent(Event.SwitchSite) },
            )
        },
        isLoading = isPageLoading && state.allTags.isEmpty(),
        isEmpty = isEmptyResult,
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            state = pullState,
            isRefreshing = isPageLoading,
            onRefresh = { onEvent(Event.PullRefresh) },
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(chunkedTags) { rowTags ->
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowTags.forEach { tag ->
                            key(tag.tags.orEmpty()) {
                                TagChip(
                                    tag = tag,
                                    onClick = {
                                        onEvent(Event.SelectTag(tag.tags))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun TagChip(
    tag: Tags,
    onClick: () -> Unit
) {
    Text(
        text = "#${tag.tags.orEmpty()} (${tag.count ?: 0})",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
