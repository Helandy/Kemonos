package su.afk.kemonos.creatorPost.presenter.util

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

/**
 * Получить один кадр из видео по URL.
 * Сначала смотрим в кэш, потом — сеть.
 *
 * @param url      – ссылка на mp4
 * @param timeUs   – время в микросекундах (0 = первый кадр)
 */
fun getVideoFrame(url: String, timeUs: Long = 0L): Bitmap? {
    /** проверяем кэш */
    VideoFrameCache.get(url)?.let { cached ->
        return cached
    }

    /** если в кэше нет — тянем из сети */
    val retriever = MediaMetadataRetriever()
    val bitmap = try {
        retriever.setDataSource(url, HashMap())
        retriever.getFrameAtTime(
            timeUs,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        )
    } finally {
        runCatching { retriever.release() }
    }

    if (bitmap != null) {
        VideoFrameCache.put(url, bitmap)
    }

    return bitmap
}
