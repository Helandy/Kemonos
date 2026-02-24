package su.afk.kemonos.profile.presenter.blacklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.blacklist.AuthorsBlacklistState.*
import su.afk.kemonos.ui.components.creator.CreatorListItem
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthorsBlacklistScreen(
    state: State,
    effect: Flow<Effect>,
    onEvent: (Event) -> Unit,
) {
    val filteredItems = if (state.query.isBlank()) {
        state.items
    } else {
        val query = state.query.trim()
        state.items.filter { item ->
            item.creatorName.contains(query, ignoreCase = true) ||
                    item.service.contains(query, ignoreCase = true) ||
                    item.creatorId.contains(query, ignoreCase = true)
        }
    }

    BaseScreen(
        isScroll = false,
        isLoading = state.isLoading,
        contentPadding = PaddingValues(horizontal = 8.dp),
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.profile_authors_blacklist),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
        onRetry = { onEvent(Event.Retry) },
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onEvent(Event.QueryChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
            label = { Text(stringResource(R.string.profile_authors_blacklist_search)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            }
        )

        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.profile_authors_blacklist_empty),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn {
                items(
                    items = filteredItems,
                    key = { "${it.service}:${it.creatorId}" }
                ) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CreatorListItem(
                                dateMode = state.uiSettingModel.dateFormatMode,
                                service = item.service,
                                id = item.creatorId,
                                name = item.creatorName,
                                onClick = {
                                    onEvent(
                                        Event.OpenProfile(
                                            service = item.service,
                                            creatorId = item.creatorId
                                        )
                                    )
                                }
                            )
                        }

                        IconButton(
                            onClick = {
                                onEvent(
                                    Event.RequestRemoveAuthor(item)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.profile_authors_blacklist_remove)
                            )
                        }
                    }
                }
            }
        }

        state.pendingRemoveAuthor?.let { author ->
            AlertDialog(
                onDismissRequest = { onEvent(Event.DismissRemoveAuthor) },
                title = { Text(stringResource(R.string.profile_authors_blacklist_remove_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.profile_authors_blacklist_remove_message,
                            author.creatorName
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onEvent(Event.ConfirmRemoveAuthor) }) {
                        Text(stringResource(R.string.profile_authors_blacklist_remove_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(Event.DismissRemoveAuthor) }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
