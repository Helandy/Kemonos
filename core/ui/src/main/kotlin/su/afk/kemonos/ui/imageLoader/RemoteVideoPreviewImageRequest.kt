package su.afk.kemonos.ui.imageLoader

import android.content.Context
import coil3.annotation.ExperimentalCoilApi
import coil3.network.CacheStrategy
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade

private const val REMOTE_VIDEO_PREVIEW_CACHE_PREFIX = "remote_video_preview:"

fun buildRemoteVideoPreviewImageRequest(
    context: Context,
    url: String,
    dataUrl: String = url,
): ImageRequest {
    val cacheKey = "$REMOTE_VIDEO_PREVIEW_CACHE_PREFIX$url"

    return ImageRequest.Builder(context)
        .data(dataUrl)
        .memoryCacheKey(cacheKey)
        .diskCacheKey(cacheKey)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .build()
}

@OptIn(ExperimentalCoilApi::class)
object SuccessOnlyImageCacheStrategy : CacheStrategy {
    private val delegate = CacheStrategy.DEFAULT

    override suspend fun read(
        cacheResponse: coil3.network.NetworkResponse,
        networkRequest: coil3.network.NetworkRequest,
        options: coil3.request.Options,
    ): CacheStrategy.ReadResult = delegate.read(
        cacheResponse = cacheResponse,
        networkRequest = networkRequest,
        options = options,
    )

    override suspend fun write(
        cacheResponse: coil3.network.NetworkResponse?,
        networkRequest: coil3.network.NetworkRequest,
        networkResponse: coil3.network.NetworkResponse,
        options: coil3.request.Options,
    ): CacheStrategy.WriteResult {
        if (networkResponse.code >= 400) {
            return CacheStrategy.WriteResult.DISABLED
        }
        return delegate.write(
            cacheResponse = cacheResponse,
            networkRequest = networkRequest,
            networkResponse = networkResponse,
            options = options,
        )
    }
}
