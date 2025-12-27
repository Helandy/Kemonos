package su.afk.kemonos.creatorPost.domain.useCase

import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import su.afk.kemonos.creatorPost.api.domain.model.VideoInfo
import su.afk.kemonos.storage.api.video.IVideoInfoUseCase
import javax.inject.Inject
import javax.inject.Named

/**
 * Получение информации о видео файле
 * - Продолжительность
 * - Размер
 * - Сохранение в бд
 * */
class GetVideoInfoUseCase @Inject constructor(
    @Named("VideoInfoClient") private val http: OkHttpClient,
    private val cache: IVideoInfoUseCase
) {
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    suspend operator fun invoke(url: String, name: String): VideoInfo = withContext(Dispatchers.IO) {
        val cached = cache.get(name)
        if (cached != null) return@withContext cached

        val head = Request.Builder().url(url).head().build()
        val sizeBytes = http.newCall(head).execute().use { resp ->
            resp.header("Content-Length")?.toLongOrNull() ?: -1L
        }

        val retriever = MediaMetadataRetriever()
        val duration = try {
            retriever.setDataSource(url, HashMap())
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } finally {
            runCatching { retriever.release() }
        }

        val videoInfo = VideoInfo(durationMs = duration, sizeBytes = sizeBytes)
        cache.upsert(name, videoInfo)
        videoInfo
    }
}