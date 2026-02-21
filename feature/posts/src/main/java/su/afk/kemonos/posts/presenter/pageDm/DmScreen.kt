package su.afk.kemonos.posts.presenter.pageDm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.domain.model.dms.DmDomain
import su.afk.kemonos.posts.presenter.pageDm.DmState.*
import su.afk.kemonos.posts.presenter.pageDm.DmState.State
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.components.dm.DmCreatorUi
import su.afk.kemonos.ui.components.dm.DmItem
import su.afk.kemonos.ui.components.dm.DmUiItem
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DmScreen(
    state: State,
    effect: Flow<Effect>,
    site: SelectedSite,
    siteSwitching: Boolean,
    onEvent: (Event) -> Unit,
) {
    val dms = state.dms.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current

    val isPageLoading = dms.loadState.refresh is LoadState.Loading
    val isBusy = isPageLoading || siteSwitching

    BaseScreen(
        topBarWindowInsets = WindowInsets(0),
        topBarScroll = TopBarScroll.EnterAlways,
        contentPadding = PaddingValues(horizontal = 8.dp),
        isScroll = false,
        topBar = {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(Event.SearchQueryChanged(it)) },
                label = { Text(text = stringResource(R.string.search)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        onEvent(Event.SearchSubmitted)
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
        isLoading = isPageLoading,
    ) {
        DmContent(
            dms = dms,
            dateMode = state.uiSettingModel.dateFormatMode,
            onProfileClick = { service, id ->
                onEvent(Event.NavigateToProfile(service, id))
            },
            onRetry = { dms.retry() },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun DmContent(
    dms: LazyPagingItems<DmDomain>,
    dateMode: su.afk.kemonos.preferences.ui.DateFormatMode,
    onProfileClick: (service: String, id: String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedDmHash by remember { mutableStateOf<String?>(null) }
    val isEmpty = dms.itemCount == 0 && dms.loadState.refresh !is LoadState.Loading

    if (isEmpty) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.dm_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = dms.itemCount,
            key = { index -> dms[index]?.hash ?: "dm_$index" }
        ) { index ->
            val dmDomain = dms[index] ?: return@items
            val expanded = expandedDmHash == dmDomain.hash
            val dm = DmUiItem(
                hash = dmDomain.hash,
                content = dmDomain.content,
                published = dmDomain.published,
                creator = DmCreatorUi(
                    service = dmDomain.service,
                    id = dmDomain.artistId,
                    name = dmDomain.artistName,
                    updated = dmDomain.artistUpdated,
                )
            )
            DmItem(
                dateMode = dateMode,
                dm = dm,
                expanded = expanded,
                onClick = {
                    expandedDmHash = if (expanded) null else dm.hash
                },
                onCreatorClick = { creator ->
                    onProfileClick(creator.service, creator.id)
                },
            )
        }

        when (dms.loadState.append) {
            is LoadState.Loading -> {
                item("append_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            is LoadState.Error -> {
                item("append_error") {
                    FilledTonalButton(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
