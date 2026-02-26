package su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.header

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.creator.header.CreatorHeader
import su.afk.kemonos.ui.date.toUiDateTime
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorScreenTopBar(
    profile: Profile?,
    dateFormatMode: DateFormatMode,
    platformUrl: String?,
    scrollBehavior: TopAppBarScrollBehavior?,
    onBack: () -> Unit,
    onToggleSearch: () -> Unit,
    onShare: () -> Unit,
    onOpenPlatform: (String) -> Unit,
    isInBlacklist: Boolean,
    onToggleBlacklist: () -> Unit,
) {
    val extrasVisible by remember(scrollBehavior) {
        derivedStateOf {
            scrollBehavior?.state?.heightOffset?.let { it >= -1f } ?: true
        }
    }
    val isAtStartOfPage by remember(scrollBehavior) {
        derivedStateOf {
            scrollBehavior?.state?.let { appBarState ->
                abs(appBarState.heightOffset) < 0.5f &&
                        abs(appBarState.contentOffset) < 0.5f
            } ?: true
        }
    }

    Column {
        CreatorCenterBackTopBar(
            title = profile?.name.orEmpty(),
            showTitle = !isAtStartOfPage,
            onBack = onBack,
            scrollBehavior = scrollBehavior,
            actions = {
                if (profile == null) return@CreatorCenterBackTopBar
                CreatorTopBarActions(
                    profile = profile,
                    dateFormatMode = dateFormatMode,
                    platformUrl = platformUrl,
                    onToggleSearch = onToggleSearch,
                    onShare = onShare,
                    onOpenPlatform = onOpenPlatform,
                    isInBlacklist = isInBlacklist,
                    onToggleBlacklist = onToggleBlacklist,
                )
            }
        )

        AnimatedVisibility(visible = extrasVisible) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                if (profile == null) return@AnimatedVisibility

                AnimatedVisibility(visible = isAtStartOfPage) {
                    CreatorHeader(
                        service = profile.service,
                        creatorId = profile.id,
                        creatorName = profile.name,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreatorTopBarActions(
    profile: Profile,
    dateFormatMode: DateFormatMode,
    platformUrl: String?,
    onToggleSearch: () -> Unit,
    onShare: () -> Unit,
    onOpenPlatform: (String) -> Unit,
    isInBlacklist: Boolean,
    onToggleBlacklist: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = onToggleSearch) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(R.string.search),
        )
    }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
        )
    }

    CreatorTopBarMenu(
        expanded = expanded,
        updated = profile.updated,
        dateFormatMode = dateFormatMode,
        platformUrl = platformUrl,
        onDismiss = { expanded = false },
        onShare = {
            expanded = false
            onShare()
        },
        onOpenPlatform = { link ->
            expanded = false
            onOpenPlatform(link)
        },
        isInBlacklist = isInBlacklist,
        onToggleBlacklist = {
            expanded = false
            onToggleBlacklist()
        }
    )
}

@Composable
private fun CreatorTopBarMenu(
    expanded: Boolean,
    updated: String?,
    dateFormatMode: DateFormatMode,
    platformUrl: String?,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onOpenPlatform: (String) -> Unit,
    isInBlacklist: Boolean,
    onToggleBlacklist: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.share)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null
                )
            },
            onClick = onShare
        )

        platformUrl?.let { link ->
            DropdownMenuItem(
                text = { Text(stringResource(R.string.open_platform_profile)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null
                    )
                },
                onClick = { onOpenPlatform(link) }
            )
        }

        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        if (isInBlacklist) {
                            R.string.author_blacklist_remove_action
                        } else {
                            R.string.author_blacklist_add_action
                        }
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null
                )
            },
            onClick = onToggleBlacklist
        )

        updated?.let { upd ->
            DropdownMenuItem(
                text = { Text(upd.toUiDateTime(dateFormatMode)) },
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
