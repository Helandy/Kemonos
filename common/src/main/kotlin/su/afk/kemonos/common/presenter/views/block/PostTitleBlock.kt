package su.afk.kemonos.common.presenter.views.block

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.toUiDateTime

@Composable
fun PostTitleBlock(
    title: String,
    published: String?,
    edited: String?,
    added: String?,
) {
    var expanded by remember { mutableStateOf(false) }

    val showEdited = !edited.isNullOrEmpty() && edited != published
    val showAdded = !added.isNullOrEmpty() && added != published && added != edited

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        /** Вертикально: Info */
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = stringResource(R.string.info),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentSize(Alignment.TopEnd)
            ) {
                published?.let {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "📅 ${it.toUiDateTime()}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = { }
                    )
                }
                if (showEdited) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "✏️ ${edited.toUiDateTime()}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = { }
                    )
                }
                if (showAdded) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "📥 ${added.toUiDateTime()}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = { }
                    )
                }
            }
        }
    }
}