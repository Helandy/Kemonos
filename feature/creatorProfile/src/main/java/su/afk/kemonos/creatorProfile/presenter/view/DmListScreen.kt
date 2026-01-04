package su.afk.kemonos.creatorProfile.presenter.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import java.time.LocalDateTime

@Composable
fun DmListScreen(
    dms: List<Dm>,
    modifier: Modifier = Modifier
) {
    var expandedDmHash by remember { mutableStateOf<String?>(null) }

    val sortedDms = dms.sortedByDescending { dm ->
        runCatching { LocalDateTime.parse(dm.published) }.getOrNull() ?: LocalDateTime.MIN
    }

    if (sortedDms.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.dm_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedDms, key = { it.hash }) { dm ->
                val expanded = expandedDmHash == dm.hash
                DmItem(
                    dm = dm,
                    expanded = expanded,

                    onClick = {
                        expandedDmHash = if (expanded) null else dm.hash
                    }
                )
            }
        }
    }
}

@Composable
fun DmItem(
    dm: Dm,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.clickable { onClick() }.animateContentSize().padding(12.dp)) {
            /** Делаем текст выделяемым */
            key(expanded) {
                SelectionContainer {
                    Text(
                        text = dm.content,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (expanded) Int.MAX_VALUE else 4,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.dm_published, dm.published.toUiDateTime()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}