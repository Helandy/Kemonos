package su.afk.kemonos.creatorPost.domain.useCase

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.creatorPost.domain.model.media.VideoMeta
import su.afk.kemonos.storage.api.repository.media.IStoreMediaInfoRepository
import su.afk.kemonos.storage.api.videoPreview.IVideoFrameCache
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named

/**
 * Получение информации о видео файле
 * - Продолжительность
 * - Размер
 * - Сохранение в бд
 * */
class GetMediaMetaUseCase @Inject constructor(
    @Named("VideoInfoClient") private val http: OkHttpClient,
    private val infoCache: IStoreMediaInfoRepository,
    private val frameCache: IVideoFrameCache,
) {
    private val inFlight = ConcurrentHashMap<String, Deferred<VideoMeta>>()

    suspend operator fun invoke(
        url: String,
        path: String,
        loadFrame: Boolean,
        frameTimeUs: Long = IVideoFrameCache.DEFAULT_TIME_US,
    ): VideoMeta = coroutineScope {
        val safeTimeUs = frameTimeUs.coerceAtLeast(0L)

        val key = "meta|p=$path|frame=$loadFrame|t=$safeTimeUs"

        val deferred = inFlight.computeIfAbsent(key) {
            async(Dispatchers.IO) {
                try {
                    val cachedInfo = infoCache.get(url)

                    // --- FRAME ---
                    val frame: Bitmap? = if (loadFrame) {
                        frameCache.getOrLoad(path, safeTimeUs) {
                            MediaMetadataRetriever().use { r ->
                                r.setDataSource(url, HashMap())
                                r.safeFrame(safeTimeUs)
                            }
                        }
                    } else null

                    // --- DURATION ---
                    val durationMs = cachedInfo?.durationMs?.takeIf { it >= 0 } ?: runCatching {
                        MediaMetadataRetriever().use { r ->
                            r.setDataSource(url, HashMap())
                            r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                ?.toLongOrNull() ?: -1L
                        }
                    }.getOrDefault(-1L)

                    // --- SIZE ---
                    val sizeBytes = cachedInfo?.sizeBytes?.takeIf { it >= 0 } ?: getSizeBytesByRange(http, url)

                    val info = MediaInfo(durationMs = durationMs, sizeBytes = sizeBytes)
                    infoCache.upsert(url, info)

                    VideoMeta(info = info, frame = frame)
                } finally {
                    inFlight.remove(key)
                }
            }
        }

        deferred.await()
    }
}

private fun getSizeBytesByRange(http: OkHttpClient, url: String): Long {
    val req = Request.Builder()
        .url(url)
        .get()
        .header("Range", "bytes=0-0")
        .build()

    return runCatching {
        http.newCall(req).execute().use { resp ->
            val cr = resp.header("Content-Range") // bytes 0-0/1234567
            cr?.substringAfterLast('/')?.toLongOrNull()
                ?: resp.header("Content-Length")?.toLongOrNull()
                ?: -1L
        }
    }.getOrDefault(-1L)
}

inline fun <T> MediaMetadataRetriever.use(block: (MediaMetadataRetriever) -> T): T {
    try {
        return block(this)
    } finally {
        runCatching { release() }
    }
}

fun MediaMetadataRetriever.safeFrame(timeUs: Long): Bitmap? {
    getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)?.let { return it }
    getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)?.let { return it }
    return getFrameAtTime(0L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
}