package su.afk.kemonos.creatorPost.domain.useCase

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.storage.api.video.IMediaInfoUseCase
import javax.inject.Inject
import javax.inject.Named

/**
 * Получение информации о видео файле
 * - Продолжительность
 * - Размер
 * - Сохранение в бд
 * */
class GetMediaInfoUseCase @Inject constructor(
    @Named("VideoInfoClient") private val http: OkHttpClient,
    private val cache: IMediaInfoUseCase
) {
    suspend operator fun invoke(url: String): MediaInfo = withContext(Dispatchers.IO) {
        val key = url
        cache.get(key)?.let { return@withContext it }

        val sizeBytes = runCatching {
            val head = Request.Builder().url(url).head().build()
            http.newCall(head).execute().use { resp ->
                if (!resp.isSuccessful) -1L
                else resp.header("Content-Length")?.toLongOrNull() ?: -1L
            }
        }.getOrDefault(-1L)

        val durationMs = runCatching {
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(url, HashMap())
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: -1L
            }
        }.getOrDefault(-1L)

        val info = MediaInfo(durationMs = durationMs, sizeBytes = sizeBytes)
        cache.upsert(key, info)
        info
    }
}
