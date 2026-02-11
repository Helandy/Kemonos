package su.afk.kemonos.creatorPost.presenter.view.audio

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.isAudioFile
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.domain.models.AttachmentDomain

internal fun LazyListScope.postAudioSection(
    attachments: List<AttachmentDomain>,
    audioInfo: Map<String, MediaInfoState>,
    onInfoRequested: (String) -> Unit,
    onPlay: (AttachmentDomain) -> Unit,
    onDownload: (AttachmentDomain) -> Unit,
    showHeader: Boolean = true,
) {
    val audios = attachments.asSequence()
        .filter { isAudioFile(it.path) }
        .distinctBy { "${it.server.orEmpty()}|${it.path}" }
        .toList()

    if (audios.isEmpty()) return

    if (showHeader) {
        item(key = "audio_header") {
            Text(
                text = stringResource(R.string.audio_file),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp, top = 16.dp)
            )
        }
    }

    items(
        count = audios.size,
        key = { idx ->
            val item = audios[idx]
            "audio:${item.server.orEmpty()}:${item.path}"
        }
    ) { idx ->
        val a = audios[idx]
        val url = "${a.server}/data${a.path}"

        AudioInfoItem(
            audio = a,
            infoState = audioInfo[url],
            requestInfo = onInfoRequested,
            onPlay = onPlay,
            onDownload = onDownload,
        )
    }
}
