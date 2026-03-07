package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorPost.domain.media.model.MediaInfoState
import su.afk.kemonos.domain.models.VideoDomain
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.uiUtils.format.formatDurationMinutesSeconds
import su.afk.kemonos.ui.uiUtils.format.formatSizeMegabytesRounded
import su.afk.kemonos.ui.video.openVideoExternally

/**
 * Один видеоролик со статичным кадром-превью.
 * Превью берётся через встроенный в Coil `VideoFrameDecoder`,
 * кэшом управляет сама библиотека.
 */
@Composable
internal fun VideoPreviewItem(
    showPreview: Boolean,
    blurImage: Boolean,
    previewAspectRatio: Float,
    previewServerUrl: String,
    useExternalMetaData: Boolean,
    requestKey: Any? = null,
    video: VideoDomain,
    requestInfo: (server: String, path: String) -> Unit,
    infoState: MediaInfoState?,
    onDownloadClick: (url: String, fileName: String) -> Unit,
) {
    val context = LocalContext.current
    val url = remember(video) { "${video.server}/data${video.path}" }
    val remotePreviewReady = when (val state = infoState) {
        is MediaInfoState.Success -> state.data.videoInfo != null
        else -> false
    }
    val waitingRemoteInfo = useExternalMetaData &&
            (infoState == null || infoState is MediaInfoState.Loading)
    val useRemotePreview = useExternalMetaData && remotePreviewReady
    val useSelfPreview = !useExternalMetaData || (!waitingRemoteInfo && !useRemotePreview)
    val waitingMeta = !hasResolvedMeta(infoState)
    val thumbLoading = waitingRemoteInfo || waitingMeta
    var remotePreviewDelayPassed by remember(url, showPreview, useRemotePreview) {
        mutableStateOf(false)
    }

    LaunchedEffect(showPreview, useRemotePreview, url) {
        if (!showPreview || !useRemotePreview) {
            remotePreviewDelayPassed = false
            return@LaunchedEffect
        }
        remotePreviewDelayPassed = true
    }

    LaunchedEffect(requestKey, url) {
        requestInfo(video.server, video.path)
    }

    Column(Modifier.padding(vertical = 4.dp)) {
        Box(
            Modifier.fillMaxWidth()
                .aspectRatio(previewAspectRatio)
        ) {
            if (useRemotePreview) {
                RemotePreview(
                    showPreview = showPreview && remotePreviewDelayPassed,
                    previewServerUrl = previewServerUrl,
                    videoPath = video.path,
                    contentDescription = video.name,
                    blurImage = blurImage,
                )
            } else if (useSelfPreview) {
                SelfPreview(
                    showPreview = showPreview,
                    url = url,
                    videoPath = video.path,
                    contentDescription = video.name,
                    blurImage = blurImage,
                    context = context,
                    onLoadingChanged = {},
                )
            } else {
                SelfPreview(
                    showPreview = false,
                    url = url,
                    videoPath = video.path,
                    contentDescription = video.name,
                    blurImage = blurImage,
                    context = context,
                    onLoadingChanged = {},
                )
            }

            VideoPreviewLoading(thumbLoading)

            /** Play Button */
            Button(
                onClick = { openVideoExternally(context, url, video.name) },
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(62.dp)
                    .align(Alignment.Center)
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }

            /** ⬇️ Download */
            FilledIconButton(
                onClick = { onDownloadClick(url, video.name) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(42.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = stringResource(R.string.download),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        /** Инфа о видео */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                video.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            VideoPreviewMeta(infoState)
        }
    }
}

@Composable
private fun BoxScope.VideoPreviewLoading(visible: Boolean) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(48.dp),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun VideoPreviewMeta(infoState: MediaInfoState?) {
    if (infoState !is MediaInfoState.Success) return

    val data = infoState.data
    val durationMs = data.videoInfo?.durationSeconds?.takeIf { it > 0 }?.times(1000)
        ?: data.mediaInfo?.durationMs
        ?: -1L
    val sizeBytes = data.videoInfo?.sizeBytes?.takeIf { it >= 0 }
        ?: data.mediaInfo?.sizeBytes
        ?: -1L

    val dur = formatDurationMinutesSeconds(durationMs) ?: "?"
    val sizeStr = formatSizeMegabytesRounded(sizeBytes) ?: "?"

    Text(
        text = "⏱ $dur   📦 $sizeStr",
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private fun hasResolvedMeta(infoState: MediaInfoState?): Boolean {
    if (infoState !is MediaInfoState.Success) return false

    val data = infoState.data
    val durationMs = data.videoInfo?.durationSeconds?.takeIf { it > 0 }?.times(1000)
        ?: data.mediaInfo?.durationMs
        ?: -1L
    val sizeBytes = data.videoInfo?.sizeBytes?.takeIf { it >= 0 }
        ?: data.mediaInfo?.sizeBytes
        ?: -1L

    return durationMs > 0L && sizeBytes >= 0L
}
