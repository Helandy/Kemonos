package su.afk.kemonos.videoPlayer.presenter.imageView

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.gif.GifDecoder
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import su.afk.kemonos.common.R
import su.afk.kemonos.common.error.view.DefaultErrorContent
import su.afk.kemonos.domain.models.ErrorItem
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ImagePreviewScreen(
    imageUrl: String,
    onBack: () -> Unit,
    minScale: Float = 1f,
    maxScale: Float = 4f,
    doubleTapScale: Float = 3f
) {
    var retryKey by remember { mutableIntStateOf(0) }

    var container by remember { mutableStateOf(IntSize.Zero) }

    /** «живые» значения под пальцами */
    var scale by remember { mutableFloatStateOf(minScale) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    fun clampOffset(x: Float, y: Float, s: Float): Pair<Float, Float> {
        if (container.width == 0 || container.height == 0) return x to y
        val maxX = (container.width * (s - 1f)) / 2f
        val maxY = (container.height * (s - 1f)) / 2f
        return x.coerceIn(-maxX, maxX) to y.coerceIn(-maxY, maxY)
    }

    /** Pinch/пан/вращение — обновляем «живые» значения, а таргеты подгоняем под них (без анимации) */
    val transformState = rememberTransformableState { zoom, pan, _ ->
        val newScale = (scale * zoom).coerceIn(minScale, maxScale)
        val nx = offsetX + pan.x
        val ny = offsetY + pan.y
        val (clampedX, clampedY) = clampOffset(nx, ny, newScale)

        scale = newScale
        offsetX = clampedX
        offsetY = clampedY
    }

    /** Двойной тап — считаем таргеты и анимации «к ним» */
    fun onDoubleTap(tap: Offset) {
        val willZoomIn = scale < (minScale + 0.01f)
        val targetS = if (willZoomIn)
            doubleTapScale.coerceIn(minScale, maxScale)
        else
            minScale

        val cx = container.width / 2f
        val cy = container.height / 2f
        val k = (targetS / scale) - 1f
        val tx = offsetX + (cx - tap.x) * k
        val ty = offsetY + (cy - tap.y) * k
        val (clampedX, clampedY) = clampOffset(tx, ty, targetS)

        scale = targetS
        offsetX = clampedX
        offsetY = clampedY
    }

    /** Coil ImageLoader */
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }


    /** пересоздаём запрос при retryKey++ */
    val request = remember(imageUrl, retryKey, container) {
        val w = (container.width * maxScale).roundToInt().coerceAtLeast(1)
        val h = (container.height * maxScale).roundToInt().coerceAtLeast(1)

        val maxSide = 4096
        val scaleDown = maxOf(w.toFloat() / maxSide, h.toFloat() / maxSide, 1f)
        val rw = (w / scaleDown).roundToInt()
        val rh = (h / scaleDown).roundToInt()

        ImageRequest.Builder(context)
            .data(imageUrl)
            .size(rw, rh)
            .precision(Precision.EXACT)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onSizeChanged { container = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (abs(scale - minScale) < 0.01f) onBack()
                    },
                    onDoubleTap = { tap -> onDoubleTap(tap) }
                )
            }
            .transformable(transformState)
    ) {
        SubcomposeAsyncImage(
            model = request,
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY,
                    rotationZ = 0f
                ),
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            error = {
                DefaultErrorContent(
                    onBack = onBack,
                    errorItem = ErrorItem(
                        title = stringResource(R.string.err_title_generic),
                        message = stringResource(R.string.err_msg_generic),
                    ),
                    onRetry = { retryKey++ }
                )
            }
        )
    }
}