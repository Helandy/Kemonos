package su.afk.kemonos.common.imageLoader

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay

/**
 * Универсальный Async-загрузчик изображения с:
 * - индикатором «долгой» загрузки (появляется через 3 с);
 * - обработкой ошибок;
 * - кастомным placeholder-ом.
 */
@Composable
fun AsyncImageWithStatus(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
    errorText: ((Throwable?) -> String)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    onSuccessSize: ((Size) -> Unit)? = null,
    imageLoader: ImageLoader = LocalAppImageLoader.current,
) {
    var showLoader by remember { mutableStateOf(false) }

    val painter = rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
    )
    val state by painter.state.collectAsStateWithLifecycle()

    /* -------- «длинная» загрузка -------- */
    LaunchedEffect(state) {
        if (state is AsyncImagePainter.State.Loading) {
            delay(1_500)
            if (state is AsyncImagePainter.State.Loading) showLoader = true
        } else {
            showLoader = false
        }
    }

    /** -------------- UI -------------- */
    Box(modifier, contentAlignment = alignment) {
        when (state) {
            is AsyncImagePainter.State.Success -> {
                onSuccessSize?.invoke(painter.intrinsicSize)

                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.matchParentSize(),
                    contentScale = contentScale,
                    alignment = alignment
                )
            }

            is AsyncImagePainter.State.Loading -> {
                if (placeholder != null) {
                    placeholder()
                } else {
                    Image(
                        painter = painter,
                        contentDescription = contentDescription,
                        modifier = Modifier.matchParentSize(),
                        contentScale = contentScale,
                        alignment = alignment
                    )
                }
                if (showLoader) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is AsyncImagePainter.State.Error -> {
                val t = (state as AsyncImagePainter.State.Error).result.throwable
                Log.e("AsyncImageWithStatus", "Image load error", t)

                val msg = errorText?.invoke(t) ?: (t?.localizedMessage ?: "Error loading")
                Box(
                    Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        msg,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            /** State.Empty */
            else -> placeholder?.invoke()
        }
    }
}
