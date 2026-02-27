package su.afk.kemonos.setting.presenter.view.uiSetting

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.CreatorProfileTabKey
import su.afk.kemonos.profile.R

@Composable
internal fun CreatorProfileTabsOrderEditor(
    value: List<CreatorProfileTabKey>,
    onChange: (List<CreatorProfileTabKey>) -> Unit,
) {
    val listState = rememberLazyListState()
    var draggingKey by remember { mutableStateOf<CreatorProfileTabKey?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val currentItems by rememberUpdatedState(value)
    val onChangeLatest by rememberUpdatedState(onChange)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(
            items = value,
            key = { _, tab -> tab.name }
        ) { index, tab ->
            Row(
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
                                if (targetIndex == -1 || targetIndex == activeIndex) {
                                    return@detectDragGesturesAfterLongPress
                                }

                                val reordered = currentItems.toMutableList().apply {
                                    add(targetIndex, removeAt(activeIndex))
                                }
                                onChangeLatest(reordered)

                                // Keep drag continuous after crossing to a neighbor item.
                                dragOffsetY -= (targetInfo.offset - activeInfo.offset)
                            }
                        )
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(tab.titleRes()),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (index == 0) return@IconButton
                                    val reordered = value.toMutableList().apply {
                                        add(index - 1, removeAt(index))
                                    }
                                    onChange(reordered)
                                },
                                enabled = index > 0,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = stringResource(R.string.settings_move_up)
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (index == value.lastIndex) return@IconButton
                                    val reordered = value.toMutableList().apply {
                                        add(index + 1, removeAt(index))
                                    }
                                    onChange(reordered)
                                },
                                enabled = index < value.lastIndex,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
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
