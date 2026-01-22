package su.afk.kemonos.creatorPost.presenter.view.audio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.isAudioFile
import su.afk.kemonos.domain.models.AttachmentDomain

internal fun LazyListScope.postAudioSection(
    attachments: List<AttachmentDomain>,
    onPlay: (AttachmentDomain) -> Unit,
    onDownload: (AttachmentDomain) -> Unit,
) {
    val audios = attachments.filter { isAudioFile(it.path) }
    if (audios.isEmpty()) return

    item(key = "audio_header") {
        Text(
            text = stringResource(R.string.audio_file),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }

    items(
        count = audios.size,
        key = { idx ->
            val item = audios[idx]
            "audio:${item.server.orEmpty()}:${item.path}"
        }
    ) { idx ->
        val a = audios[idx]
        val title = a.name?.takeIf { it.isNotBlank() } ?: a.path.substringAfterLast('/')

        ListItem(
            headlineContent = {
                Text(title, maxLines = 3, overflow = TextOverflow.Ellipsis)
            },
            leadingContent = {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
            },
            trailingContent = {
                IconButton(onClick = { onDownload(a) }) {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = stringResource(R.string.download)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlay(a) }
        )
        HorizontalDivider()
    }
}