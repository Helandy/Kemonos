package su.afk.kemonos.creatorPost.data.media

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import su.afk.kemonos.creatorPost.domain.media.IRemoteMediaProbe
import su.afk.kemonos.utils.DispatcherProvider
import javax.inject.Inject
import javax.inject.Named

internal class RemoteMediaProbe @Inject constructor(
    @param:Named("VideoInfoClient") private val http: OkHttpClient,
    private val dispatchers: DispatcherProvider,
) : IRemoteMediaProbe {

    override suspend fun durationMs(url: String): Long = withContext(dispatchers.io) {
        runCatching {
            MediaMetadataRetriever().use { r ->
                r.setDataSource(url, HashMap())
                r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: -1L
            }
        }.getOrDefault(-1L)
    }

    /** Размер через `Range: bytes=0-0`, без скачивания тела. */
    override suspend fun sizeBytes(url: String): Long = withContext(dispatchers.io) {
        val req = Request.Builder()
            .url(url)
            .get()
            .header("Range", "bytes=0-0")
            .build()

        runCatching {
            http.newCall(req).execute().use { resp ->
                val cr = resp.header("Content-Range") // bytes 0-0/1234567
                cr?.substringAfterLast('/')?.toLongOrNull()
                    ?: resp.header("Content-Length")?.toLongOrNull()
                    ?: -1L
            }
        }.getOrDefault(-1L)
    }
}

private inline fun <T> MediaMetadataRetriever.use(block: (MediaMetadataRetriever) -> T): T {
    try {
        return block(this)
    } finally {
        runCatching { release() }
    }
}
