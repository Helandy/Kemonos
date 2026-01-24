package su.afk.kemonos.storage.api.video

import android.graphics.Bitmap

interface IVideoFrameCache {

    /** Детеминированный ключ для url + timeUs + параметров компрессии/ресайза */
    fun makeKey(url: String, timeUs: Long = DEFAULT_TIME_US): String

    suspend fun get(key: String): Bitmap?

    /** Сохранить в кэш (диск + опционально память) */
    suspend fun put(key: String, bitmap: Bitmap)

    /** Самый удобный метод: верни из кэша или вычисли один раз (под mutex per key) */
    suspend fun getOrLoad(
        url: String,
        timeUs: Long = DEFAULT_TIME_US,
        loader: suspend () -> Bitmap?
    ): Bitmap?

    suspend fun clear()

    companion object {
        const val DEFAULT_TIME_US: Long = 1_000_000L
    }
}