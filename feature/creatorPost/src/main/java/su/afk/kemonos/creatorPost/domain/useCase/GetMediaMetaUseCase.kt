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
import su.afk.kemonos.storage.api.media.IMediaInfoUseCase
import su.afk.kemonos.storage.api.video.IVideoFrameCache
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
    private val infoCache: IMediaInfoUseCase,
    private val frameCache: IVideoFrameCache,
) {
    private val inFlight = ConcurrentHashMap<String, Deferred<VideoMeta>>()

    suspend operator fun invoke(
        url: String,
        loadFrame: Boolean,
        frameTimeUs: Long = IVideoFrameCache.DEFAULT_TIME_US,
        loadSize: Boolean = true,
    ): VideoMeta = coroutineScope {
        val deferred = inFlight.computeIfAbsent(url) {
            async(Dispatchers.IO) {
                try {
                    val cachedInfo = infoCache.get(url)
                    val frame: Bitmap? = if (loadFrame) frameCache.getOrLoad(url, frameTimeUs) {
                        MediaMetadataRetriever().use { r ->
                            r.setDataSource(url, HashMap())
                            r.getFrameAtTime(frameTimeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        }
                    } else null

                    val durationMs = cachedInfo?.durationMs ?: runCatching {
                        MediaMetadataRetriever().use { r ->
                            r.setDataSource(url, HashMap())
                            r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                ?.toLongOrNull() ?: -1L
                        }
                    }.getOrDefault(-1L)

                    val sizeBytes = if (loadSize) {
                        cachedInfo?.sizeBytes ?: runCatching {
                            val head = Request.Builder().url(url).head().build()
                            http.newCall(head).execute().use { resp ->
                                if (!resp.isSuccessful) -1L
                                else resp.header("Content-Length")?.toLongOrNull() ?: -1L
                            }
                        }.getOrDefault(-1L)
                    } else {
                        cachedInfo?.sizeBytes ?: -1L
                    }

                    val info = MediaInfo(durationMs = durationMs, sizeBytes = sizeBytes)
                    infoCache.upsert(url, info)

                    VideoMeta(info = info, frame = frame)
                } finally {
                    inFlight.remove(url)
                }
            }
        }

        deferred.await()
    }
}