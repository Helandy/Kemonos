package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
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
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Precision
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.common.R
import su.afk.kemonos.common.error.view.DefaultErrorContent
import su.afk.kemonos.common.imageLoader.LocalAppImageLoader
import su.afk.kemonos.common.imageLoader.imageProgress.IMAGE_PROGRESS_REQUEST_ID_HEADER
import su.afk.kemonos.common.util.formatBytes
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Effect
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Event
import su.afk.kemonos.domain.models.ErrorItem
import kotlin.math.abs
import kotlin.math.roundToInt

// todo добавить и сюда кнопку назад плавующую слева сверху
@Composable
internal fun ImageViewScreen(state: ImageViewState.State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val minScale: Float = 1f
    val maxScale: Float = 4f
    val doubleTapScale: Float = 3f

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
    val imageLoader = LocalAppImageLoader.current

    val headers = remember(state.requestId, state.reloadKey) {
        NetworkHeaders.Builder()
            .set(IMAGE_PROGRESS_REQUEST_ID_HEADER, state.requestId)
            .set("X-Kemonos-Retry-Key", state.reloadKey.toString())
            .build()
    }

    /** пересоздаём запрос при Retry из ViewModel */
    val request = remember(state.imageUrl, state.reloadKey, container) {
        val w = (container.width * maxScale).roundToInt().coerceAtLeast(1)
        val h = (container.height * maxScale).roundToInt().coerceAtLeast(1)

        val maxSide = 4096
        val scaleDown = maxOf(w.toFloat() / maxSide, h.toFloat() / maxSide, 1f)
        val rw = (w / scaleDown).roundToInt()
        val rh = (h / scaleDown).roundToInt()

        ImageRequest.Builder(context)
            .data(state.imageUrl)
            .size(rw, rh)
            .precision(Precision.EXACT)
            .diskCachePolicy(CachePolicy.ENABLED)
            .httpHeaders(headers)
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
                        if (abs(scale - minScale) < 0.01f) onEvent(Event.Back)
                    },
                    onDoubleTap = { tap -> onDoubleTap(tap) }
                )
            }
            .transformable(transformState)
    ) {
        key(state.reloadKey) {
            SubcomposeAsyncImage(
                model = request,
                imageLoader = imageLoader,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                onSuccess = {
                    onEvent(Event.ImageLoaded)
                },
                onError = {
                    onEvent(Event.ImageFailed(it.result.throwable))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY,
                        rotationZ = 0f
                    ),
                success = { SubcomposeAsyncImageContent() },
                loading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            val isDeterminate =
                                state.contentLength > 0L && state.bytesRead > 0L && state.progress > 0f

                            if (isDeterminate) {
                                CircularProgressIndicator(
                                    progress = { state.progress },
                                    modifier = Modifier.size(72.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 6.dp,
                                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(72.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 6.dp,
                                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                                    strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap,
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            val shouldShowBytes =
                                state.bytesRead > 0L || state.contentLength > 0L

                            if (shouldShowBytes) {
                                val text = buildString {
                                    if (state.bytesRead > 0L) {
                                        append(formatBytes(state.bytesRead))
                                    }
                                    if (state.contentLength > 0L) {
                                        if (state.bytesRead > 0L) append(" / ")
                                        append(formatBytes(state.contentLength))
                                    }
                                }

                                if (text.isNotBlank()) {
                                    Text(text = text, style = MaterialTheme.typography.bodyMedium)
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.loading),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                },
                error = {
                    DefaultErrorContent(
                        onBack = {
                            onEvent(Event.Back)
                        },
                        errorItem = state.errorItem ?: ErrorItem(
                            title = stringResource(R.string.err_title_generic),
                            message = stringResource(R.string.err_msg_generic),
                        ),
                        onRetry = { onEvent(Event.Retry) }
                    )
                }
            )
        }
    }
}
