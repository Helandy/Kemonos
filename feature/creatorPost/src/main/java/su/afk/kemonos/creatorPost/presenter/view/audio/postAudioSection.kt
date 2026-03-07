package su.afk.kemonos.creatorPost.presenter.view.audio

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorPost.domain.media.model.MediaInfoState
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.uiUtils.format.isAudioFile

internal fun LazyListScope.postAudioSection(
    audios: List<AttachmentDomain>,
    fallbackBaseUrl: String?,
    audioInfo: Map<String, MediaInfoState>,
    onInfoRequested: (String?, String) -> Unit,
    onPlay: (AttachmentDomain) -> Unit,
    onDownload: (AttachmentDomain) -> Unit,
    onShare: (AttachmentDomain) -> Unit,
    showHeader: Boolean = true,
) {
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
        val server = a.server ?: fallbackBaseUrl

        AudioInfoItem(
            audio = a,
            infoState = audioInfo[a.path],
            server = server,
            path = a.path,
            requestInfo = onInfoRequested,
            onPlay = onPlay,
            onDownload = onDownload,
            onShare = onShare,
        )
    }
}

internal fun List<AttachmentDomain>.distinctAudioAttachments(): List<AttachmentDomain> {
    return asSequence()
        .filter { isAudioFile(it.path) }
        .distinctBy { "${it.server.orEmpty()}|${it.path}" }
        .toList()
}
