package su.afk.kemonos.profile.presenter.setting.view.cache

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.model.CacheTimeUi
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.profile.R
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@Composable
internal fun CacheRow(
    dateFormatMode: DateFormatMode,
    title: String,
    time: CacheTimeUi,
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

                val last = time.lastMs?.toUiDateTime(dateFormatMode)
                val next = time.nextMs?.toUiDateTime(dateFormatMode)

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

@Preview("PreviewCacheRow")
@Composable
private fun PreviewCacheRow() {
    KemonosPreviewScreen {
        CacheRow(
            dateFormatMode = DateFormatMode.DD_MM_YYYY,
            title = "Title",
            time = CacheTimeUi(
                lastMs = 1000.toLong(),
                nextMs = 1000.toLong(),
                isFresh = true,
            ),
            onClear = {},
            busy = false
        )
    }
}