package su.afk.kemonos.creatorPost.presenter.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.common.util.openVideoExternally
import su.afk.kemonos.creatorPost.domain.model.video.VideoInfoState
import su.afk.kemonos.creatorPost.presenter.util.VideoThumbnail
import su.afk.kemonos.domain.models.VideoDomain
import kotlin.math.roundToInt

/**
 * Один видеоролик со статичным кадром-превью.
 * Превью берётся через встроенный в Coil `VideoFrameDecoder`,
 * кэшом управляет сама библиотека.
 */
@Composable
internal fun VideoInfoPreview(
    video: VideoDomain,
    observeVideoInfo: (String, String) -> StateFlow<VideoInfoState>,
) {
    val url = remember(video) { "${video.server}/data${video.path}" }
    val infoState by observeVideoInfo(url, video.name).collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(Modifier.padding(vertical = 12.dp)) {
        Text(video.name, style = MaterialTheme.typography.titleSmall)

        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            /** Картинка */
            VideoThumbnail(
                url = url,
                modifier = Modifier.matchParentSize()
            )

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
        }

        /** Инфа о видео */
        if (infoState is VideoInfoState.Success) {
            val data = (infoState as VideoInfoState.Success).data
            val dur = "%d:%02d".format(
                data.durationMs / 60000,
                (data.durationMs / 1000) % 60
            )
            val sizeMb = if (data.sizeBytes >= 0) (data.sizeBytes / 1024f / 1024f) else -1f
            val sizeStr = if (sizeMb >= 0) "${sizeMb.roundToInt()} MB" else "?"

            Text(
                text = "⏱ $dur   📦 $sizeStr",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
            )
        }
    }
}