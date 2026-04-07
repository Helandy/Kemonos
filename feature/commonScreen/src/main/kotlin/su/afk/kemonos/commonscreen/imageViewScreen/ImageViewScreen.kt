package su.afk.kemonos.commonscreen.imageViewScreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Precision
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Effect
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.Event
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.view.DefaultErrorContent
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.ui.imageLoader.LocalAppImageLoader
import su.afk.kemonos.ui.imageLoader.imageProgress.IMAGE_PROGRESS_REQUEST_ID_HEADER
import su.afk.kemonos.ui.shared.ShareActions
import su.afk.kemonos.ui.shared.shareRemoteMedia
import su.afk.kemonos.ui.shared.view.ShareLoadingOverlay
import su.afk.kemonos.ui.toast.toast
import su.afk.kemonos.ui.uiUtils.size.formatBytes
import kotlin.math.roundToInt

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 4f
private const val DOUBLE_TAP_SCALE = 3f
private const val MAX_REQUEST_SIDE = 4096

@Composable
internal fun ImageViewScreen(
    state: ImageViewState.State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
    imageLoader: ImageLoader,
) {
    var showActionsMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val shareState = rememberShareUiState()
    val gestureState = rememberImageGestureState(resetKey = state.imageUrl to state.selectedIndex)
    val transformState = rememberTransformableState { zoom, pan, _ ->
        gestureState.onTransform(zoom = zoom, pan = pan)
    }

    val hasGallery = state.imageUrls.size > 1
    val canGoPrev = state.selectedIndex > 0
    val canGoNext = state.selectedIndex < state.imageUrls.lastIndex

    collectImageViewEffects(effect = effect, context = context)

    val request = rememberImageRequest(
        context = context,
        imageUrl = state.imageUrl,
        requestId = state.requestId,
        reloadKey = state.reloadKey,
        container = gestureState.container,
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onSizeChanged(gestureState::onContainerChanged)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tap -> gestureState.onDoubleTap(tap) }
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
                alignment = Alignment.Center,
                onSuccess = {
                    onEvent(Event.ImageLoaded)
                },
                onError = {
                    onEvent(Event.ImageFailed(it.result.throwable))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = gestureState.scale,
                        scaleY = gestureState.scale,
                        translationX = gestureState.offsetX,
                        translationY = gestureState.offsetY,
                        rotationZ = 0f
                    ),
                success = { SubcomposeAsyncImageContent() },
                loading = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val thumbnailUrl = state.thumbnailUrls[state.imageUrl]
                        if (!thumbnailUrl.isNullOrBlank()) {
                            AsyncImageWithStatus(
                                model = thumbnailUrl,
                                contentDescription = null,
                                imageLoader = LocalAppImageLoader.current,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(top = 8.dp, start = 64.dp, end = 64.dp),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            DelayedImageLoadingContent(state = state)
                        }
                    }
                },
                error = {
                    DefaultErrorContent(
                        onBack = { onEvent(Event.Back) },
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

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
        ) {
            IconButton(
                onClick = { showActionsMenu = true },
                enabled = !state.imageUrl.isNullOrBlank(),
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.more_actions),
                )
            }

            DropdownMenu(
                expanded = showActionsMenu,
                onDismissRequest = { showActionsMenu = false },
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.download)) },
                    onClick = {
                        showActionsMenu = false
                        onEvent(Event.DownloadCurrentImage)
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.share)) },
                    enabled = !shareState.inProgress,
                    onClick = {
                        showActionsMenu = false
                        val imageUrl = state.imageUrl ?: return@DropdownMenuItem
                        launchShare(
                            scope = scope,
                            context = context,
                            shareState = shareState,
                            url = imageUrl,
                            fileName = imageUrl.toUri().lastPathSegment,
                            mime = "image/*"
                        )
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.copy_link)) },
                    onClick = {
                        showActionsMenu = false
                        onEvent(Event.CopyCurrentImageLink)
                    },
                )
            }
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

        ShareLoadingOverlay(
            visible = shareState.inProgress,
            bytesRead = shareState.bytesRead,
            totalBytes = shareState.totalBytes
        )
    }
}

@Composable
private fun DelayedImageLoadingContent(state: ImageViewState.State) {
    var showLoading by remember(state.requestId) { mutableStateOf(false) }

    LaunchedEffect(state.requestId) {
        delay(220)
        showLoading = true
    }

    if (showLoading) {
        ImageLoadingContent(state = state)
    }
}

@Composable
private fun ImageLoadingContent(state: ImageViewState.State) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .widthIn(min = 190.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 3.dp,
                )
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(Modifier.height(8.dp))

            val isDeterminate =
                state.contentLength > 0L && state.bytesRead > 0L && state.progress > 0f

            if (isDeterminate) {
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))

            val transferText = buildTransferText(
                bytesRead = state.bytesRead,
                contentLength = state.contentLength,
            )

            if (transferText != null) {
                Text(
                    text = transferText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun collectImageViewEffects(
    effect: Flow<Effect>,
    context: Context,
) {
    LaunchedEffect(effect, context) {
        effect.collect { item ->
            when (item) {
                is Effect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, item.url.toUri())
                    context.startActivity(intent)
                }

                is Effect.ShowToast -> {
                    context.toast(item.message)
                }

                is Effect.DownloadToast -> {
                    val safeName = item.fileName.trim().takeIf { it.isNotBlank() }
                    val message = if (safeName != null) {
                        context.getString(R.string.download_started_named, safeName)
                    } else {
                        context.getString(R.string.download_started)
                    }
                    context.toast(message)
                }

                is Effect.CopyUrl -> {
                    ShareActions.copyToClipboard(
                        context = context,
                        label = context.getString(R.string.copy),
                        text = item.url,
                    )
                    context.toast(context.getString(R.string.copy_link))
                }
            }
        }
    }
}

@Composable
private fun rememberImageRequest(
    context: Context,
    imageUrl: String?,
    requestId: String,
    reloadKey: Int,
    container: IntSize,
): ImageRequest {
    val headers = remember(requestId, reloadKey) {
        NetworkHeaders.Builder()
            .set(IMAGE_PROGRESS_REQUEST_ID_HEADER, requestId)
            .set("X-Kemonos-Retry-Key", reloadKey.toString())
            .build()
    }

    return remember(imageUrl, reloadKey, container, headers, context) {
        val requestSize = calculateRequestSize(container)
        ImageRequest.Builder(context)
            .data(imageUrl)
            .size(requestSize.width, requestSize.height)
            .precision(Precision.EXACT)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .httpHeaders(headers)
            .build()
    }
}

private fun calculateRequestSize(container: IntSize): IntSize {
    val w = (container.width * MAX_SCALE).roundToInt().coerceAtLeast(1)
    val h = (container.height * MAX_SCALE).roundToInt().coerceAtLeast(1)

    val scaleDown = maxOf(w.toFloat() / MAX_REQUEST_SIDE, h.toFloat() / MAX_REQUEST_SIDE, 1f)
    val rw = (w / scaleDown).roundToInt()
    val rh = (h / scaleDown).roundToInt()

    return IntSize(rw, rh)
}

private fun buildTransferText(bytesRead: Long, contentLength: Long): String? {
    if (bytesRead <= 0L && contentLength <= 0L) return null

    val text = buildString {
        if (bytesRead > 0L) {
            append(formatBytes(bytesRead))
        }
        if (contentLength > 0L) {
            if (bytesRead > 0L) append(" / ")
            append(formatBytes(contentLength))
        }
    }

    return text.takeIf { it.isNotBlank() }
}

@Composable
private fun rememberImageGestureState(resetKey: Any): ImageGestureState {
    val state = remember {
        ImageGestureState(
            minScale = MIN_SCALE,
            maxScale = MAX_SCALE,
            doubleTapScale = DOUBLE_TAP_SCALE,
        )
    }

    LaunchedEffect(resetKey) {
        state.reset()
    }

    return state
}

@Stable
private class ImageGestureState(
    private val minScale: Float,
    private val maxScale: Float,
    private val doubleTapScale: Float,
) {
    var container by mutableStateOf(IntSize.Zero)
        private set

    var scale by mutableFloatStateOf(minScale)
        private set
    var offsetX by mutableFloatStateOf(0f)
        private set
    var offsetY by mutableFloatStateOf(0f)
        private set

    fun reset() {
        scale = minScale
        offsetX = 0f
        offsetY = 0f
    }

    fun onContainerChanged(size: IntSize) {
        container = size
        val (clampedX, clampedY) = clampOffset(
            x = offsetX,
            y = offsetY,
            scale = scale,
            container = container,
        )
        offsetX = clampedX
        offsetY = clampedY
    }

    fun onTransform(zoom: Float, pan: Offset) {
        val newScale = (scale * zoom).coerceIn(minScale, maxScale)
        val nx = offsetX + pan.x
        val ny = offsetY + pan.y
        val (clampedX, clampedY) = clampOffset(
            x = nx,
            y = ny,
            scale = newScale,
            container = container,
        )

        scale = newScale
        offsetX = clampedX
        offsetY = clampedY
    }

    fun onDoubleTap(tap: Offset) {
        val willZoomIn = scale < (minScale + 0.01f)
        val targetScale = if (willZoomIn) {
            doubleTapScale.coerceIn(minScale, maxScale)
        } else {
            minScale
        }

        val (targetOffsetX, targetOffsetY) = calculateDoubleTapOffset(
            tap = tap,
            container = container,
            currentScale = scale,
            targetScale = targetScale,
            currentOffsetX = offsetX,
            currentOffsetY = offsetY,
        )

        scale = targetScale
        offsetX = targetOffsetX
        offsetY = targetOffsetY
    }
}

private fun calculateDoubleTapOffset(
    tap: Offset,
    container: IntSize,
    currentScale: Float,
    targetScale: Float,
    currentOffsetX: Float,
    currentOffsetY: Float,
): Pair<Float, Float> {
    val cx = container.width / 2f
    val cy = container.height / 2f
    val k = (targetScale / currentScale) - 1f

    val tx = currentOffsetX + (cx - tap.x) * k
    val ty = currentOffsetY + (cy - tap.y) * k

    return clampOffset(
        x = tx,
        y = ty,
        scale = targetScale,
        container = container,
    )
}

private fun clampOffset(
    x: Float,
    y: Float,
    scale: Float,
    container: IntSize,
): Pair<Float, Float> {
    if (container.width == 0 || container.height == 0) return x to y

    val maxX = (container.width * (scale - 1f)) / 2f
    val maxY = (container.height * (scale - 1f)) / 2f

    return x.coerceIn(-maxX, maxX) to y.coerceIn(-maxY, maxY)
}

@Composable
private fun rememberShareUiState(): ShareUiState = remember { ShareUiState() }

@Stable
private class ShareUiState {
    var inProgress by mutableStateOf(false)
        private set
    var bytesRead by mutableLongStateOf(0L)
        private set
    var totalBytes by mutableLongStateOf(0L)
        private set

    fun start() {
        bytesRead = 0L
        totalBytes = 0L
        inProgress = true
    }

    fun finish() {
        inProgress = false
    }

    fun onProgress(bytesRead: Long, totalBytes: Long) {
        this.bytesRead = bytesRead
        this.totalBytes = totalBytes
    }
}

private fun launchShare(
    scope: CoroutineScope,
    context: Context,
    shareState: ShareUiState,
    url: String,
    fileName: String?,
    mime: String,
) {
    if (shareState.inProgress) return

    scope.launch {
        shareState.start()
        val shared = try {
            shareRemoteMedia(
                context = context,
                url = url,
                fileName = fileName,
                mime = mime,
                onProgress = shareState::onProgress,
            )
        } finally {
            shareState.finish()
        }

        if (!shared) {
            context.toast(context.getString(R.string.share_failed))
        }
    }
}
