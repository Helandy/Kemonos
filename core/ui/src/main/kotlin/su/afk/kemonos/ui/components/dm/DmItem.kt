package su.afk.kemonos.ui.components.dm

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.creator.CreatorListItem
import su.afk.kemonos.ui.date.toUiDateTime

@Composable
fun DmItem(
    dateMode: DateFormatMode,
    dm: DmUiItem,
    expanded: Boolean,
    onClick: () -> Unit,
    onCreatorClick: ((DmCreatorUi) -> Unit)? = null,
) {
    Column {
        dm.creator?.let { creator ->
            CreatorListItem(
                dateMode = dateMode,
                service = creator.service,
                id = creator.id,
                name = creator.name,
                updated = creator.updated,
                onClick = { onCreatorClick?.invoke(creator) }
            )
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .clickable { onClick() }
                    .animateContentSize()
                    .padding(12.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = dm.content,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (expanded) Int.MAX_VALUE else 5,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.dm_published, dm.published.toUiDateTime(dateMode)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
