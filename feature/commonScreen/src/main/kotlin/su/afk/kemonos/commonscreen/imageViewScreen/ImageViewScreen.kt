package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
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
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Precision
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Effect
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Event
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.view.DefaultErrorContent
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.imageLoader.imageProgress.IMAGE_PROGRESS_REQUEST_ID_HEADER
import su.afk.kemonos.ui.uiUtils.size.formatBytes
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun ImageViewScreen(
    state: ImageViewState.State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
    imageLoader: ImageLoader,
) {
    val minScale: Float = 1f
    val maxScale: Float = 4f
    val doubleTapScale: Float = 3f

    var container by remember { mutableStateOf(IntSize.Zero) }

    /** «живые» значения под пальцами */
    var scale by remember { mutableFloatStateOf(minScale) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val hasGallery = state.imageUrls.size > 1
    val canGoPrev = state.selectedIndex > 0
    val canGoNext = state.selectedIndex < state.imageUrls.lastIndex

    LaunchedEffect(state.imageUrl, state.selectedIndex) {
        // При смене картинки начинаем с дефолтного состояния (без зума/панорамы)
        scale = minScale
        offsetX = 0f
        offsetY = 0f
    }

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
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
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
                            url = state.imageUrl,
                        ),
                        onRetry = { onEvent(Event.Retry) }
                    )
                }
            )
        }

        IconButton(
            onClick = { onEvent(Event.Back) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
            )
        }

        if (hasGallery) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                tonalElevation = 2.dp,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    IconButton(
                        onClick = { onEvent(Event.PrevImage) },
                        enabled = canGoPrev,
                        modifier = Modifier
                            .width(72.dp)
                            .height(52.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous image",
                        )
                    }

                    Text(
                        text = "${state.selectedIndex + 1} / ${state.imageUrls.size}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )

                    IconButton(
                        onClick = { onEvent(Event.NextImage) },
                        enabled = canGoNext,
                        modifier = Modifier
                            .width(72.dp)
                            .height(52.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next image",
                        )
                    }
                }
            }
        }
    }
}
