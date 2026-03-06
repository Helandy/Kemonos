package su.afk.kemonos.creatorProfile.presenter.communityChat.view

import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

@Composable
internal fun InlineVideoPreview(
    url: String,
    modifier: Modifier = Modifier,
) {
    val viewHolder = remember { mutableStateOf<VideoView?>(null) }

    DisposableEffect(url) {
        onDispose {
            viewHolder.value?.stopPlayback()
            viewHolder.value = null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    mediaPlayer.setVolume(0f, 0f)
                    start()
                }
                setOnCompletionListener { start() }
                setVideoURI(url.toUri())
                viewHolder.value = this
            }
        },
        update = { videoView ->
            if (videoView.tag != url) {
                videoView.tag = url
                videoView.setVideoURI(url.toUri())
            }
            if (!videoView.isPlaying) {
                videoView.start()
            }
            viewHolder.value = videoView
        }
    )
}
