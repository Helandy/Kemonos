package su.afk.kemonos.creatorPost.presenter.util

import android.graphics.Bitmap

object VideoFrameCache {

    private const val MAX_ENTRIES = 50

    private val cache = object : LinkedHashMap<String, Bitmap>(MAX_ENTRIES, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Bitmap>?): Boolean {
            return size > MAX_ENTRIES
        }
    }

    @Synchronized
    fun get(url: String): Bitmap? = cache[url]

    @Synchronized
    fun put(url: String, bitmap: Bitmap) {
        cache[url] = bitmap
    }
}