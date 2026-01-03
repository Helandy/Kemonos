package su.afk.kemonos.profile.presenter.setting.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.model.CacheTimeUi
import su.afk.kemonos.profile.R

@Composable
internal fun CacheRow(
    title: String,
    time: CacheTimeUi,
    formatDateTime: (Long) -> String,
    onClear: () -> Unit,
    busy: Boolean,
    showDivider: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)

                val last = time.lastMs?.let(formatDateTime)
                val next = time.nextMs?.let(formatDateTime)

                if (last != null) {
                    Text(
                        stringResource(R.string.settings_cache_last, last),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (next != null) {
                    Text(
                        stringResource(R.string.settings_cache_next, next),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(
                onClick = onClear,
                enabled = !busy
            ) {
                if (busy) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(stringResource(R.string.settings_cache_clear))
            }
        }

        Spacer(Modifier.height(8.dp))
        if (showDivider) {
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
        }
    }
}
