package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.video.openVideoExternally
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.creatorPost.domain.model.video.VideoThumbState
import su.afk.kemonos.domain.models.VideoDomain
import kotlin.math.roundToInt

/**
 * Один видеоролик со статичным кадром-превью.
 * Превью берётся через встроенный в Coil `VideoFrameDecoder`,
 * кэшом управляет сама библиотека.
 */
@Composable
internal fun VideoPreviewItem(
    showPreview: Boolean,
    blurImage: Boolean,
    video: VideoDomain,
    infoState: MediaInfoState?,
    requestInfo: (server: String, path: String) -> Unit,
    thumbState: VideoThumbState,
    requestThumb: (server: String, path: String) -> Unit,
    onDownloadClick: (url: String, fileName: String) -> Unit,
) {
    val url = remember(video) { "${video.server}/data${video.path}" }

    if (showPreview) {
        LaunchedEffect(url) {
            requestInfo(video.server, video.path)
            requestThumb(video.server, video.path)
        }
    }

    val context = LocalContext.current
    val thumbLoading = thumbState is VideoThumbState.Loading || thumbState is VideoThumbState.Idle

    Column(Modifier.padding(vertical = 4.dp)) {
        Box(
            Modifier.fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            when (thumbState) {
                is VideoThumbState.Success -> {
                    AsyncImageWithStatus(
                        model = thumbState.bitmap,
                        contentDescription = video.name,
                        modifier = Modifier.matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .then(if (blurImage) Modifier.blur(14.dp) else Modifier),
                        contentScale = ContentScale.Crop,
                    )
                }

                is VideoThumbState.Error -> {
                    Box(
                        Modifier.matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                }

                VideoThumbState.Idle,
                VideoThumbState.Loading -> {
                    Box(
                        Modifier.matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                }
            }

            if (showPreview) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = thumbLoading,
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
            if (infoState is MediaInfoState.Success) {
                val data = infoState.data
                val dur = "%d:%02d".format(
                    data.durationMs / 60000,
                    (data.durationMs / 1000) % 60
                )
                val sizeMb = if (data.sizeBytes >= 0) (data.sizeBytes / 1024f / 1024f) else -1f
                val sizeStr = if (sizeMb >= 0) "${sizeMb.roundToInt()} MB" else "?"

                Text(
                    text = "⏱ $dur   📦 $sizeStr",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}