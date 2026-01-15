package su.afk.kemonos.creatorPost.presenter.util

import android.graphics.Bitmap
import android.util.LruCache

object VideoFrameCache {

    private const val MAX_CACHE_SIZE_BYTES = 48 * 1024 * 1024

    private val cache = object : LruCache<String, Bitmap>(MAX_CACHE_SIZE_BYTES) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.allocationByteCount
        }
    }

    @Synchronized
    fun get(url: String): Bitmap? = cache.get(url)

    @Synchronized
    fun put(url: String, bitmap: Bitmap) {
        cache.put(url, bitmap)
    }
}