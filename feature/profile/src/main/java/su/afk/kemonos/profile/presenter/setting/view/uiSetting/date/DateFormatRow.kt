package su.afk.kemonos.profile.presenter.setting.view.uiSetting.date

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DateFormatMode
import java.util.*

@Composable
internal fun DateFormatRow(
    title: String,
    value: DateFormatMode,
    onChange: (DateFormatMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )

            // Якорь справа
            Box(
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .clickable { expanded = true }
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = value.example(Locale.getDefault()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 0.dp, y = 8.dp),
                    modifier = Modifier.wrapContentWidth(Alignment.End)
                ) {
                    DateFormatMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = mode.example(Locale.getDefault()),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = mode.pattern,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                expanded = false
                                onChange(mode)
                            }
                        )
                    }
                }
            }
        }
    }
}