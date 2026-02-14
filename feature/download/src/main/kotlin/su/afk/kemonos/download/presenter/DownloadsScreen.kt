package su.afk.kemonos.download.presenter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.download.R
import su.afk.kemonos.download.presenter.model.DownloadUiItem
import java.text.DateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DownloadsScreen(
    state: DownloadsState.State,
    effect: Flow<DownloadsState.Effect>,
    onEvent: (DownloadsState.Event) -> Unit,
) {
    effect
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.downloads_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DownloadsState.Event.BackClick) }) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            state.items.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.downloads_empty),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = state.items,
                        key = { it.downloadId },
                    ) { item ->
                        DownloadItemCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadItemCard(
    item: DownloadUiItem,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(text = "${stringResource(R.string.downloads_status)}: ${item.statusLabel}")

            if (item.reasonLabel != null) {
                val code = item.reasonCode?.toString() ?: "-"
                Text(text = "${stringResource(R.string.downloads_error)}: ${item.reasonLabel} (code=$code)")
            }

            val total = if (item.totalBytes > 0L) formatBytes(item.totalBytes) else "?"
            Text(
                text = "${stringResource(R.string.downloads_size)}: ${formatBytes(item.bytesDownloaded)} / $total"
            )

            Text(
                text = "${stringResource(R.string.downloads_speed)}: ${formatSpeed(item.speedBytesPerSec)}"
            )

            if (item.totalBytes > 0L) {
                val progress = (item.bytesDownloaded.toFloat() / item.totalBytes.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }

            if (!item.mediaType.isNullOrBlank()) {
                Text(text = "MIME: ${item.mediaType}")
            }

            if (!item.localUri.isNullOrBlank()) {
                Text(
                    text = "Local: ${item.localUri}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (!item.remoteUri.isNullOrBlank()) {
                Text(
                    text = "URL: ${item.remoteUri}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (item.lastModifiedMs != null && item.lastModifiedMs > 0L) {
                Text(text = "${stringResource(R.string.downloads_updated)}: ${formatDate(item.lastModifiedMs)}")
            }

            HorizontalDivider(modifier = Modifier.padding(top = 2.dp))
            Text(text = "ID: ${item.downloadId}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun formatDate(timestampMs: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(timestampMs))

private fun formatSpeed(bytesPerSec: Long): String =
    if (bytesPerSec <= 0L) "0 B/s" else "${formatBytes(bytesPerSec)}/s"

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt().coerceAtMost(units.lastIndex)
    val value = bytes / 1024.0.pow(digitGroups.toDouble())
    return String.format("%.1f %s", value, units[digitGroups])
}
