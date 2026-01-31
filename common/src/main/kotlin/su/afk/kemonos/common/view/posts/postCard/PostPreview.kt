package su.afk.kemonos.common.view.posts.postCard

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.afk.kemonos.common.R
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.presenter.androidView.clearHtml
import su.afk.kemonos.common.video.LocalVideoFrameCache
import su.afk.kemonos.common.view.posts.postCard.model.PreviewState
import su.afk.kemonos.common.view.posts.postCard.placeHolder.PreviewPlaceholder

@Composable
internal fun PostPreview(
    preview: PreviewState,
    imgBaseUrl: String,
    title: String?,
    textPreview: String?,
) {
    when (preview) {
        is PreviewState.Image -> {
            AsyncImageWithStatus(
                model = "$imgBaseUrl/thumbnail/data${preview.path}",
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        is PreviewState.Video -> {
            val cache = LocalVideoFrameCache.current

            val path = preview.url.orEmpty()

            val frame by produceState<Bitmap?>(initialValue = null, key1 = path) {
                value = null
                if (path.isBlank()) return@produceState

                // если быстро скроллят — отменится, и диск не перегружается
                kotlinx.coroutines.delay(200)

                value = withContext(Dispatchers.IO) {
                    cache.getByPath(path)
                }
            }
            if (frame != null) {
                Image(
                    bitmap = frame!!.asImageBitmap(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                PreviewPlaceholder(text = stringResource(R.string.video_file))
            }
        }


        PreviewState.Audio -> {
            PreviewPlaceholder(text = stringResource(R.string.audio_file))
        }

        PreviewState.Empty -> {
            if (textPreview.isNullOrBlank()) {
                PreviewPlaceholder(text = "")
            } else {
                PreviewPlaceholder(textPreview.clearHtml())
            }
        }
    }
}

@Composable
internal fun CornerBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(99.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 2.dp,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
        )
    }
}