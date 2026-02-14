package su.afk.kemonos.creatorProfile.presenter.view.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import su.afk.kemonos.domain.models.Tag

@Composable
internal fun ProfileTabsBar(
    tabs: List<ProfileTab>,
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit,
    currentTag: Tag?,
    onTagClear: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val desiredOrder = listOf(
        ProfileTab.POSTS,
        ProfileTab.ANNOUNCEMENTS,
        ProfileTab.FANCARD,
        ProfileTab.DMS,
        ProfileTab.TAGS,
        ProfileTab.LINKS
    )

    /** tabs — это список вкладок динамический */
    val orderedTabs = tabs.sortedBy { tab ->
        desiredOrder.indexOf(tab).let { if (it == -1) Int.MAX_VALUE else it }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        orderedTabs.forEach { tab ->
            val isSelected = tab == selectedTab

            /** Показываем кастомный чип если выбрали тег */
            if (tab == ProfileTab.TAGS && currentTag != null) {
                FilterChip(
                    selected = true,
                    onClick = { onTabSelected(tab) },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                currentTag.tag,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Сбросить тег",
                                modifier = Modifier
                                    .clickable { onTagClear?.invoke() }
                                    .padding(start = 2.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            } else {
                /** Обычный чип */
                FilterChip(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    label = { Text(stringResource(tab.labelRes)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}