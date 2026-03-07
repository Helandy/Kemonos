package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import su.afk.kemonos.ui.imageLoader.LocalAppImageLoader
import su.afk.kemonos.ui.imageLoader.buildRemoteVideoPreviewImageRequest
import su.afk.kemonos.ui.uiUtils.format.buildVideoPreviewUrls

@Composable
internal fun RemotePreview(
    showPreview: Boolean,
    previewServerUrl: String,
    videoPath: String,
    contentDescription: String,
    blurImage: Boolean,
) {
    val previewUrls = remember(videoPath, showPreview, previewServerUrl) {
        buildVideoPreviewUrls(
            videoPath = videoPath,
            enabled = showPreview,
            previewServerUrl = previewServerUrl,
        )
    }
    var previewIndex by remember(videoPath, previewUrls) { mutableIntStateOf(0) }
    var failedPreviewUrls by remember(videoPath, previewUrls) { mutableStateOf(emptySet<String>()) }
    val previewUrl = previewUrls.getOrNull(previewIndex)

    Box(Modifier.fillMaxSize()) {
        RemotePreviewImage(
            model = previewUrl,
            contentDescription = contentDescription,
            blurImage = blurImage,
            onPreviewUnavailable = { failedUrl ->
                val updatedFailedUrls = failedPreviewUrls + failedUrl
                failedPreviewUrls = updatedFailedUrls
                previewIndex = previewIndex.nextAvailableIndex(
                    urls = previewUrls,
                    failedUrls = updatedFailedUrls,
                )
            },
        )

        if (previewUrls.isNotEmpty()) {
            RemotePreviewCounter(
                current = previewIndex + 1,
                total = previewUrls.size,
            )
        }

        if (previewUrls.size > 1) {
            Row(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            previewIndex = previewIndex.previousIndex(previewUrls.size)
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            previewIndex = previewIndex.nextIndex(previewUrls.size)
                        }
                )
            }
        }
    }
}

@Composable
private fun RemotePreviewImage(
    model: Any?,
    contentDescription: String,
    blurImage: Boolean,
    onPreviewUnavailable: (String) -> Unit,
) {
    if (model != null) {
        val context = LocalContext.current
        var reloadKey by remember(model) { mutableIntStateOf(0) }
        var retryAttempt by remember(model) { mutableIntStateOf(0) }
        var unavailableReported by remember(model) { mutableStateOf(false) }
        val requestUrl = remember(model, reloadKey) {
            model.toString().withRetryToken(reloadKey)
        }
        val request = remember(model, context, requestUrl) {
            buildRemoteVideoPreviewImageRequest(
                context = context,
                url = model.toString(),
                dataUrl = requestUrl,
            )
        }
        val painter = rememberAsyncImagePainter(
            model = request,
            imageLoader = LocalAppImageLoader.current,
        )
        val state by painter.state.collectAsStateWithLifecycle()

        LaunchedEffect(model, state, reloadKey) {
            val errorState = state as? AsyncImagePainter.State.Error ?: return@LaunchedEffect
            val isNotFound = errorState.result.throwable.extractCoilHttpCode() == HTTP_NOT_FOUND
            if (!isNotFound) return@LaunchedEffect

            if (retryAttempt >= MAX_RETRY_SHIFT) {
                if (!unavailableReported) {
                    unavailableReported = true
                    onPreviewUnavailable(model.toString())
                }
                return@LaunchedEffect
            }

            delay(RETRY_SECONDS shl retryAttempt)
            retryAttempt++
            reloadKey++
        }

        Box(
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(if (blurImage) Modifier.blur(14.dp) else Modifier),
        ) {
            androidx.compose.foundation.Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    } else {
        Box(
            Modifier.fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}

@Composable
private fun BoxScope.RemotePreviewCounter(
    current: Int,
    total: Int,
) {
    Surface(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp),
        shape = RoundedCornerShape(48.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    ) {
        Text(
            text = "$current/$total",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

private fun Int.previousIndex(size: Int): Int {
    if (size <= 0) return 0
    return (this - 1 + size) % size
}

private fun Int.nextIndex(size: Int): Int {
    if (size <= 0) return 0
    return (this + 1) % size
}

private fun Int.nextAvailableIndex(
    urls: List<String>,
    failedUrls: Set<String>,
): Int {
    if (urls.isEmpty()) return 0

    repeat(urls.size) { offset ->
        val candidateIndex = (this + offset + 1) % urls.size
        val candidateUrl = urls.getOrNull(candidateIndex)
        if (candidateUrl != null && candidateUrl !in failedUrls) {
            return candidateIndex
        }
    }

    return this.coerceIn(0, urls.lastIndex)
}

private fun Throwable.extractCoilHttpCode(): Int? {
    val isCoilHttp = this::class.qualifiedName == "coil3.network.HttpException"
    if (!isCoilHttp) return null

    return """\b(\d{3})\b""".toRegex()
        .find(message.orEmpty())
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
}

private fun String.withRetryToken(reloadKey: Int): String {
    if (reloadKey <= 0) return this

    val separator = if ('?' in this) '&' else '?'
    return "$this${separator}retry=$reloadKey"
}

private const val HTTP_NOT_FOUND = 404
private const val RETRY_SECONDS = 1_000L
private const val MAX_RETRY_SHIFT = 4
