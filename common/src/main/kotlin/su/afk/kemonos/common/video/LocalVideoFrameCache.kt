package su.afk.kemonos.common.video

import android.graphics.Bitmap
import androidx.compose.runtime.staticCompositionLocalOf
import su.afk.kemonos.storage.api.videoPreview.IVideoFrameCache

val LocalVideoFrameCache = staticCompositionLocalOf<IVideoFrameCache> {
    error("IVideoFrameCache not provided")
}

/** Preview-мок */
object PreviewVideoFrameCache : IVideoFrameCache {

    override fun makeKey(url: String, timeUs: Long): String =
        "preview:$url@$timeUs"

    override suspend fun get(key: String): Bitmap? = null

    override suspend fun put(key: String, bitmap: Bitmap) {
        // no-op
    }

    override suspend fun getOrLoad(
        url: String,
        timeUs: Long,
        loader: suspend () -> Bitmap?
    ): Bitmap? {
        return null
    }

    override suspend fun clear() {
        // no-op
    }
}