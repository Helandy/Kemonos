package su.afk.kemonos.ui.components.dm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.R
import java.time.LocalDateTime

@Composable
fun DmListScreen(
    dateMode: DateFormatMode,
    dms: List<DmUiItem>,
    modifier: Modifier = Modifier,
    sortByPublishedDesc: Boolean = false,
    onCreatorClick: ((DmCreatorUi) -> Unit)? = null,
) {
    var expandedDmHash by remember { mutableStateOf<String?>(null) }

    val prepared = if (sortByPublishedDesc) {
        dms.sortedByDescending { dm ->
            runCatching { LocalDateTime.parse(dm.published) }.getOrNull() ?: LocalDateTime.MIN
        }
    } else {
        dms
    }

    if (prepared.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
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
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(prepared, key = { it.hash }) { dm ->
            val expanded = expandedDmHash == dm.hash
            DmItem(
                dateMode = dateMode,
                dm = dm,
                expanded = expanded,
                onCreatorClick = onCreatorClick,
                onClick = {
                    expandedDmHash = if (expanded) null else dm.hash
                }
            )
        }
    }
}
