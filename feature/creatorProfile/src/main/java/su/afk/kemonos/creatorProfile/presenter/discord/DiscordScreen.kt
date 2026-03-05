package su.afk.kemonos.creatorProfile.presenter.discord

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.CommunityScreen
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.header.CreatorCenterBackTopBar
import su.afk.kemonos.creatorProfile.presenter.discord.DiscordState.*
import su.afk.kemonos.creatorProfile.presenter.discord.DiscordState.State
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.EmptyContentCenter
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.shared.ShareActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscordScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>
) {
    val context = LocalContext.current
    val title = state.serverName.ifBlank {
        if (state.service.equals("discord", ignoreCase = true)) {
            "Discord"
        } else {
            stringResource(R.string.profile_tab_community)
        }
    }
    val filteredChannels = remember(state.channels, state.searchText) {
        val query = state.searchText.trim()
        if (query.isBlank()) {
            state.channels
        } else {
            val lowerQuery = query.lowercase()
            state.channels.filter { channel ->
                channel.name.contains(lowerQuery, ignoreCase = true) ||
                        channel.channelId.contains(lowerQuery, ignoreCase = true)
            }
        }
    }
    LaunchedEffect(effect) {
        effect.collect {
            when (it) {
                is Effect.CopyPostLink -> ShareActions.copyToClipboard(
                    context,
                    context.getString(R.string.copy_link),
                    it.message
                )
            }
        }
    }

    BaseScreen(
        isScroll = false,
        contentModifier = Modifier.padding(horizontal = 16.dp),
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CreatorCenterBackTopBar(
                title = title,
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
                actions = {
                    DiscordTopBarActions(
                        updated = state.updated,
                        dateMode = state.uiSettingModel.dateFormatMode,
                        onShare = { onEvent(Event.CopyProfileLink) },
                    )
                }
            )
        },
        isLoading = state.loading,
        onRetry = { onEvent(Event.Retry) },
        floatingActionButtonEnd = {}
    ) {
        OutlinedTextField(
            value = state.searchText,
            onValueChange = { onEvent(Event.SearchTextChanged(it)) },
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(R.string.search)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search)
                )
            },
            trailingIcon = {
                if (state.searchText.isNotBlank()) {
                    IconButton(onClick = { onEvent(Event.SearchTextChanged("")) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            }
        )

        if (state.searchText.isNotBlank() && filteredChannels.isEmpty()) {
            EmptyContentCenter()
        } else {
            CommunityScreen(
                dateMode = state.uiSettingModel.dateFormatMode,
                channels = filteredChannels,
                onOpenChannel = { channel ->
                    onEvent(Event.OpenChannel(channel.channelId))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun DiscordTopBarActions(
    updated: String?,
    dateMode: su.afk.kemonos.preferences.ui.DateFormatMode,
    onShare: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.share)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null
                )
            },
            onClick = {
                expanded = false
                onShare()
            }
        )

        updated?.let { updateDate ->
            DropdownMenuItem(
                text = { Text(updateDate.toUiDateTime(dateMode)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                },
                enabled = false,
                onClick = {}
            )
        }
    }
}
