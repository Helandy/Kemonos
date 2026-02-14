package su.afk.kemonos.common.components.posts.postCard.preview

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import su.afk.kemonos.common.R
import su.afk.kemonos.common.components.posts.postCard.model.PreviewState
import su.afk.kemonos.common.components.posts.postCard.placeHolder.PreviewPlaceholder
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.presenter.androidView.clearHtml
import su.afk.kemonos.common.video.LocalVideoFrameCache

@Composable
internal fun PostPreview(
    preview: PreviewState,
    imgBaseUrl: String,
    title: String?,
    textPreview: String?,
    blurImage: Boolean,
) {
    val imageModifier = Modifier.fillMaxSize()
        .then(if (blurImage) Modifier.blur(14.dp) else Modifier)

    when (preview) {
        is PreviewState.Image -> {
            AsyncImageWithStatus(
                model = "$imgBaseUrl/thumbnail/data${preview.path}",
                contentDescription = title,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }

        is PreviewState.Video -> {
            val cache = LocalVideoFrameCache.current

            val path = preview.url.orEmpty()

            val frame by produceState<Bitmap?>(initialValue = null, key1 = path) {
                value = null
                if (path.isBlank()) return@produceState

                /** если быстро скроллят — отменится, и диск не перегружается */
                delay(200)

                value = withContext(Dispatchers.IO) {
                    cache.getByPath(path)
                }
            }
            if (frame != null) {
                Image(
                    bitmap = frame!!.asImageBitmap(),
                    contentDescription = title,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                PreviewPlaceholder(text = stringResource(R.string.video_section))
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
