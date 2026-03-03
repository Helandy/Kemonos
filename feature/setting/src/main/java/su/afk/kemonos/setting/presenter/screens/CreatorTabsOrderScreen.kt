package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.CreatorProfileTabKey
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.common.settingsSwitchColors
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorTabsOrderScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val hiddenTabs = state.uiSettingModel.creatorProfileHiddenTabs

    SettingsScreenScaffold(
        title = stringResource(R.string.settings_ui_creator_profile_tabs_sort_title),
        onBack = { onEvent(Event.Back) },
        isLoading = false,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = false,
        topBarScroll = TopBarScroll.Pinned,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_ui_creator_profile_tabs_sort_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            val listState = rememberLazyListState()
            var draggingKey by remember { mutableStateOf<CreatorProfileTabKey?>(null) }
            var dragOffsetY by remember { mutableFloatStateOf(0f) }
            val currentItems by rememberUpdatedState(state.uiSettingModel.creatorProfileTabsOrder)

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(
                    items = state.uiSettingModel.creatorProfileTabsOrder,
                    key = { _, tab -> tab.name }
                ) { index, tab ->
                    val isPostsTab = tab == CreatorProfileTabKey.POSTS
                    val isEnabled = tab !in hiddenTabs

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                if (draggingKey == tab) {
                                    translationY = dragOffsetY
                                }
                            }
                            .pointerInput(tab) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggingKey = tab
                                        dragOffsetY = 0f
                                    },
                                    onDragCancel = {
                                        draggingKey = null
                                        dragOffsetY = 0f
                                    },
                                    onDragEnd = {
                                        draggingKey = null
                                        dragOffsetY = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetY += dragAmount.y

                                        val active = draggingKey ?: return@detectDragGesturesAfterLongPress
                                        val activeIndex = currentItems.indexOf(active)
                                        if (activeIndex == -1) return@detectDragGesturesAfterLongPress

                                        val activeInfo = listState.layoutInfo.visibleItemsInfo
                                            .firstOrNull { it.key == active.name }
                                            ?: return@detectDragGesturesAfterLongPress

                                        val activeCenter = activeInfo.offset + (activeInfo.size / 2f) + dragOffsetY
                                        val targetInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { info ->
                                            info.key != active.name &&
                                                    activeCenter in info.offset.toFloat()..(info.offset + info.size).toFloat()
                                        } ?: return@detectDragGesturesAfterLongPress

                                        val target = currentItems.firstOrNull { it.name == targetInfo.key }
                                            ?: return@detectDragGesturesAfterLongPress
                                        val targetIndex = currentItems.indexOf(target)
                                        if (targetIndex == -1 || targetIndex == activeIndex) return@detectDragGesturesAfterLongPress

                                        val reordered = currentItems.toMutableList().apply {
                                            add(targetIndex, removeAt(activeIndex))
                                        }
                                        onEvent(Event.ChangeViewSetting.EditCreatorProfileTabsOrder(reordered))
                                        dragOffsetY -= (targetInfo.offset - activeInfo.offset)
                                    },
                                )
                            }
                            .padding(vertical = 4.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DragIndicator,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 6.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(tab.titleRes()),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                if (isPostsTab) {
                                    Text(
                                        text = stringResource(R.string.settings_ui_creator_profile_tabs_posts_locked_hint),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { checked ->
                                    if (isPostsTab) return@Switch
                                    val updated = hiddenTabs.toMutableSet().apply {
                                        if (checked) remove(tab) else add(tab)
                                    }
                                    onEvent(Event.ChangeViewSetting.EditCreatorProfileHiddenTabs(updated))
                                },
                                enabled = !isPostsTab,
                                colors = settingsSwitchColors(),
                                modifier = Modifier.padding(end = 10.dp)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        if (index == 0) return@IconButton
                                        val reordered =
                                            state.uiSettingModel.creatorProfileTabsOrder.toMutableList().apply {
                                                add(index - 1, removeAt(index))
                                            }
                                        onEvent(Event.ChangeViewSetting.EditCreatorProfileTabsOrder(reordered))
                                    },
                                    enabled = index > 0,
                                    modifier = Modifier.size(32.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowUp,
                                        contentDescription = stringResource(R.string.settings_move_up)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (index == state.uiSettingModel.creatorProfileTabsOrder.lastIndex) return@IconButton
                                        val reordered =
                                            state.uiSettingModel.creatorProfileTabsOrder.toMutableList().apply {
                                                add(index + 1, removeAt(index))
                                            }
                                        onEvent(Event.ChangeViewSetting.EditCreatorProfileTabsOrder(reordered))
                                    },
                                    enabled = index < state.uiSettingModel.creatorProfileTabsOrder.lastIndex,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = stringResource(R.string.settings_move_down)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun CreatorProfileTabKey.titleRes(): Int = when (this) {
    CreatorProfileTabKey.POSTS -> R.string.settings_creator_tab_posts
    CreatorProfileTabKey.ANNOUNCEMENTS -> R.string.settings_creator_tab_announcements
    CreatorProfileTabKey.FANCARD -> R.string.settings_creator_tab_fancard
    CreatorProfileTabKey.DMS -> R.string.settings_creator_tab_dms
    CreatorProfileTabKey.TAGS -> R.string.settings_creator_tab_tags
    CreatorProfileTabKey.LINKS -> R.string.settings_creator_tab_links
    CreatorProfileTabKey.SIMILAR -> R.string.settings_creator_tab_similar
    CreatorProfileTabKey.COMMUNITY -> R.string.settings_creator_tab_community
}

@Preview(name = "Creator Tabs Order", showBackground = true)
@Composable
private fun PreviewCreatorTabsOrderScreen() {
    SettingsPreview {
        CreatorTabsOrderScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
