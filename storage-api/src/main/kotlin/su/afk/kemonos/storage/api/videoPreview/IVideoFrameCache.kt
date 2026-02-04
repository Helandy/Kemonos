package su.afk.kemonos.storage.api.videoPreview

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

    suspend fun getByPath(path: String, timeUs: Long = DEFAULT_TIME_US): Bitmap? =
        get(makeKey(path, timeUs))

    suspend fun clear()

    companion object {
        /** 2 секунда */
        const val DEFAULT_TIME_US: Long = 2_000_000L
    }
}