package su.afk.kemonos.profile.presenter.blacklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.blacklist.AuthorsBlacklistState.*
import su.afk.kemonos.profile.presenter.blacklist.view.RemoveAuthorDialog
import su.afk.kemonos.profile.presenter.blacklist.view.filteredByQuery
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.ui.components.creator.CreatorListItem
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.EmptyContentCenter
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthorsBlacklistScreen(
    state: State,
    effect: Flow<Effect>,
    onEvent: (Event) -> Unit,
) {
    val filteredItems by remember(state.items, state.query) {
        derivedStateOf {
            state.items.filteredByQuery(state.query)
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
                    contentDescription = null,
                )
            }
        )

        if (filteredItems.isEmpty()) {
            EmptyContentCenter()
        } else {
            LazyColumn {
                items(
                    items = filteredItems,
                    key = {
                        "${it.service}:${it.creatorId}"
                    },
                    contentType = { "blacklisted_author" },
                ) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CreatorListItem(
                                dateMode = state.uiSettingModel.dateFormatMode,
                                name = item.creatorName,
                                service = item.service,
                                id = item.creatorId,
                                onClick = { onEvent(Event.OpenProfile(item.service, item.creatorId)) },
                            )
                        }

                        IconButton(
                            onClick = { onEvent(Event.RequestRemoveAuthor(item)) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.profile_authors_blacklist_remove),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        state.pendingRemoveAuthor?.let { author ->
            RemoveAuthorDialog(
                author = author,
                onConfirm = { onEvent(Event.ConfirmRemoveAuthor) },
                onDismiss = { onEvent(Event.DismissRemoveAuthor) },
            )
        }
    }
}

@Preview("PreviewAuthorsBlacklistScreen")
@Composable
private fun PreviewAuthorsBlacklistScreen() {
    KemonosPreviewScreen {
        AuthorsBlacklistScreen(
            state = State(
                query = "only",
                items = listOf(
                    BlacklistedAuthor(
                        service = "onlyfans",
                        creatorId = "12345",
                        creatorName = "Creator One",
                        createdAt = 0L,
                    ),
                    BlacklistedAuthor(
                        service = "patreon",
                        creatorId = "abcde",
                        creatorName = "Artist Two",
                        createdAt = 0L,
                    ),
                ),
            ),
            effect = emptyFlow(),
            onEvent = {},
        )
    }
}