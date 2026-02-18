package su.afk.kemonos.download.presenter

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.download.R
import su.afk.kemonos.download.presenter.model.DownloadUiItem
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.date.toUiDateTimeWithTime
import su.afk.kemonos.ui.shared.ShareActions
import java.io.File
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
                        DownloadItemCard(
                            item = item,
                            dateFormatMode = state.uiSettingModel.dateFormatMode,
                            onStop = { onEvent(DownloadsState.Event.StopDownload(item.downloadId)) },
                            onRestart = { onEvent(DownloadsState.Event.RestartDownload(item.downloadId)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadItemCard(
    item: DownloadUiItem,
    dateFormatMode: DateFormatMode,
    onStop: () -> Unit,
    onRestart: () -> Unit,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            AssistChip(
                onClick = {},
                enabled = true,
                label = { Text(text = item.statusLabel) },
            )

            if (item.reasonLabel != null) {
                val code = item.reasonCode?.toString() ?: "-"
                Text(text = "${stringResource(R.string.downloads_error)}: ${item.reasonLabel} (code=$code)")
            }

            val isCompleted = item.status == DownloadManager.STATUS_SUCCESSFUL
            val isStopped = item.status == -1
            if (isCompleted) {
                val completedSize = if (item.totalBytes > 0L) item.totalBytes else item.bytesDownloaded
                Text(text = "${stringResource(R.string.downloads_size)}: ${formatBytes(completedSize)}")
            } else if (!isStopped) {
                val total = if (item.totalBytes > 0L) formatBytes(item.totalBytes) else "?"
                Text(
                    text = "${stringResource(R.string.downloads_size)}: ${formatBytes(item.bytesDownloaded)} / $total"
                )
                Text(
                    text = "${stringResource(R.string.downloads_speed)}: ${formatSpeed(item.speedBytesPerSec)}"
                )
            }

            if (!isCompleted && !isStopped && item.totalBytes > 0L) {
                val progress = (item.bytesDownloaded.toFloat() / item.totalBytes.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }

            if (!item.localUri.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                openDownloadedLocation(
                                    context = context,
                                    localUri = item.localUri,
                                )
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = item.localUri.toReadableLocalPath().withWrapHints(),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            if (!item.remoteUri.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                ShareActions.copyToClipboard(
                                    context = context,
                                    label = "download_url",
                                    text = item.remoteUri,
                                )
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = item.remoteUri.withWrapHints(),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(top = 2.dp))
            if (!isCompleted) {
                val isActive =
                    item.status == DownloadManager.STATUS_RUNNING || item.status == DownloadManager.STATUS_PENDING
                if (isStopped) {
                    FilledTonalButton(
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.downloads_action_restart))
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = onStop,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                            modifier = if (isActive) Modifier.fillMaxWidth() else Modifier.weight(1f),
                        ) {
                            Text(text = stringResource(R.string.downloads_action_stop))
                        }
                        if (!isActive) {
                            OutlinedButton(
                                onClick = onRestart,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(text = stringResource(R.string.downloads_action_restart))
                            }
                        }
                    }
                }
            }

            if (item.lastModifiedMs != null && item.lastModifiedMs > 0L) {
                Text(
                    text = item.lastModifiedMs.toUiDateTimeWithTime(dateFormatMode)
                )
            }
        }
    }
}

private fun openDownloadedLocation(
    context: Context,
    localUri: String,
) {
    val sourceUri = localUri.toUriOrNull() ?: return
    val folderUri = sourceUri.parentFolderUri()
        ?: sourceUri.asExternalStorageDocumentParentUri()

    if (folderUri != null && context.tryOpenUri(folderUri, DocumentsContract.Document.MIME_TYPE_DIR)) return
    if (context.tryOpenUri(sourceUri, null)) return

    val downloadsIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(downloadsIntent) }
}

private fun Context.tryOpenUri(
    uri: Uri,
    mimeType: String?,
): Boolean = runCatching {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (mimeType.isNullOrBlank()) {
            data = uri
        } else {
            setDataAndType(uri, mimeType)
        }
    }
    startActivity(intent)
    true
}.getOrElse { error ->
    error !is ActivityNotFoundException && false
}

private fun String.toUriOrNull(): Uri? = runCatching { Uri.parse(this) }.getOrNull()

private fun Uri.parentFolderUri(): Uri? = when (scheme?.lowercase()) {
    "file" -> {
        val parent = runCatching { File(path ?: "").parentFile }.getOrNull() ?: return null
        Uri.fromFile(parent)
    }

    else -> null
}

private fun Uri.asExternalStorageDocumentParentUri(): Uri? {
    val authority = authority ?: return null
    if (authority != EXTERNAL_STORAGE_DOCUMENTS_AUTHORITY) return null

    val docId = runCatching { DocumentsContract.getDocumentId(this) }.getOrNull() ?: return null
    val parentDocId = docId.substringBeforeLast(':', missingDelimiterValue = docId)
        .let { volume ->
            val relative = docId.substringAfter(':', "")
            val parentRelative = relative.substringBeforeLast('/', missingDelimiterValue = "")
            if (parentRelative.isBlank()) "$volume:" else "$volume:$parentRelative"
        }

    return DocumentsContract.buildDocumentUri(authority, parentDocId)
}

private fun String.toReadableLocalPath(): String {
    val uri = toUriOrNull()
    val rawPath = when (uri?.scheme?.lowercase()) {
        "file" -> uri.path
        else -> this
    } ?: return this

    val normalized = rawPath.replace('\\', '/')
    return when {
        normalized == EXTERNAL_DOWNLOADS_PATH -> "Download"
        normalized.startsWith("$EXTERNAL_DOWNLOADS_PATH/") ->
            "Download/${normalized.removePrefix("$EXTERNAL_DOWNLOADS_PATH/")}"

        normalized.startsWith(EXTERNAL_STORAGE_PREFIX) ->
            normalized.removePrefix(EXTERNAL_STORAGE_PREFIX)

        else -> normalized
    }
}

private fun String.withWrapHints(): String =
    replace("/", "/\u200B")
        .replace("?", "?\u200B")
        .replace("&", "&\u200B")
        .replace("=", "=\u200B")

private const val EXTERNAL_STORAGE_DOCUMENTS_AUTHORITY = "com.android.externalstorage.documents"
private const val EXTERNAL_STORAGE_PREFIX = "/storage/emulated/0/"
private const val EXTERNAL_DOWNLOADS_PATH = "/storage/emulated/0/Download"

private fun formatSpeed(bytesPerSec: Long): String =
    if (bytesPerSec <= 0L) "0 B/s" else "${formatBytes(bytesPerSec)}/s"

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt().coerceAtMost(units.lastIndex)
    val value = bytes / 1024.0.pow(digitGroups.toDouble())
    return String.format("%.1f %s", value, units[digitGroups])
}
