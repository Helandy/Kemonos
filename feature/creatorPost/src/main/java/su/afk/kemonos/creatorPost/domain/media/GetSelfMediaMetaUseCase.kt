package su.afk.kemonos.creatorPost.domain.media

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.repository.media.IStorageMediaInfoRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named

/**
 * Получение информации о видео файле
 * - Продолжительность
 * - Размер
 * - Сохранение в бд
 * */
internal class GetSelfMediaMetaUseCase @Inject constructor(
    @param:Named("VideoInfoClient") private val http: OkHttpClient,
    private val infoCache: IStorageMediaInfoRepository,
) {
    private val inFlight = ConcurrentHashMap<String, Deferred<MediaInfo>>()

    suspend operator fun invoke(
        site: SelectedSite,
        server: String,
        path: String,
    ): MediaInfo = coroutineScope {
        val key = "${site.name}|$path"
        val mediaUrl = "$server/data$path"

        val deferred = inFlight.computeIfAbsent(key) {
            async(Dispatchers.IO) {
                try {
                    val cachedInfo = infoCache.get(site, path)

                    // --- DURATION ---
                    val durationMs = cachedInfo?.durationMs?.takeIf { it >= 0 } ?: runCatching {
                        MediaMetadataRetriever().use { r ->
                            r.setDataSource(mediaUrl, HashMap())
                            r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                ?.toLongOrNull() ?: -1L
                        }
                    }.getOrDefault(-1L)

                    // --- SIZE ---
                    val sizeBytes = cachedInfo?.sizeBytes?.takeIf { it >= 0 } ?: getSizeBytesByRange(http, mediaUrl)

                    val info = MediaInfo(durationMs = durationMs, sizeBytes = sizeBytes)
                    infoCache.upsert(site, path, info)

                    info
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
