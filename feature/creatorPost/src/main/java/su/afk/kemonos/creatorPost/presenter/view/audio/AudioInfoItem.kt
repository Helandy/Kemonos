package su.afk.kemonos.creatorPost.presenter.view.audio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.ui.R
import kotlin.math.roundToInt

@Composable
internal fun AudioInfoItem(
    audio: AttachmentDomain,
    url: String,
    infoState: MediaInfoState?,
    requestInfo: (url: String) -> Unit,
    onPlay: (AttachmentDomain) -> Unit,
    onDownload: (AttachmentDomain) -> Unit,
) {
    LaunchedEffect(url) {
        requestInfo(url)
    }

    val title = audio.name
        ?.takeIf { it.isNotBlank() }
        ?: audio.path.substringAfterLast('/')

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlay(audio) }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ▶ Play
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(8.dp))

            // Текст
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )

                when (val state = infoState ?: MediaInfoState.Idle) {
                    MediaInfoState.Idle -> Unit

                    MediaInfoState.Loading -> {
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    is MediaInfoState.Success -> {
                        val data = state.data

                        val duration = if (data.durationMs > 0) {
                            "%d:%02d".format(
                                data.durationMs / 60000,
                                (data.durationMs / 1000) % 60
                            )
                        } else "?"

                        val sizeMb =
                            if (data.sizeBytes >= 0) data.sizeBytes / 1024f / 1024f
                            else -1f

                        val sizeStr =
                            if (sizeMb >= 0) "${sizeMb.roundToInt()} MB"
                            else "?"

                        Text(
                            text = "⏱ $duration   📦 $sizeStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    is MediaInfoState.Error -> {
                    }
                }
            }

            Spacer(Modifier.width(4.dp))

            // ⬇ Download
            IconButton(
                onClick = { onDownload(audio) },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(R.string.download),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Полоска загрузки снизу
        if (infoState is MediaInfoState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
