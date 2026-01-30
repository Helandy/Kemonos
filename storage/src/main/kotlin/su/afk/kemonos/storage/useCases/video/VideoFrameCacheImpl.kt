package su.afk.kemonos.storage.useCases.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.LruCache
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import su.afk.kemonos.common.constants.Constant.VIDEO_FRAMES_DIR_NAME
import su.afk.kemonos.preferences.ui.UiSettingKey.PREVIEW_VIDEO_SIZE_MB
import su.afk.kemonos.storage.api.video.IVideoFrameCache
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.math.roundToInt

class VideoFrameCacheImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStore: DataStore<Preferences>,
) : IVideoFrameCache {

    private val maxDiskBytes: Long = runBlocking {
        withContext(Dispatchers.IO) {
            val p = dataStore.data.first()
            val mb = p[PREVIEW_VIDEO_SIZE_MB] ?: DEFAULT_VIDEO_CACHE_MB
            mb.coerceIn(MIN_VIDEO_CACHE_MB, MAX_VIDEO_CACHE_MB).toLong() * 1024L * 1024L
        }
    }

    private val useMemory: Boolean = true
    private val maxMemoryBytes: Int = 48 * 1024 * 1024 // 48MB

    private val maxW: Int = 1280
    private val maxH: Int = 720

    private val webpQuality: Int = 90
    private val webpLossless: Boolean = false
    private val EXT = "webp"

    private val cacheDir: File = File(appContext.cacheDir, VIDEO_FRAMES_DIR_NAME).apply { mkdirs() }

    private val mutexByKey = ConcurrentHashMap<String, Mutex>()

    private val memory: LruCache<String, Bitmap>? = if (useMemory) {
        object : LruCache<String, Bitmap>(maxMemoryBytes) {
            override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount
        }
    } else null

    override fun makeKey(url: String, timeUs: Long): String {
        val raw = "$url|t=$timeUs|max=${maxW}x$maxH|q=$webpQuality|lossless=$webpLossless|fmt=$EXT"
        return raw.sha256()
    }

    override suspend fun get(key: String): Bitmap? = withContext(Dispatchers.IO) {
        memory?.get(key)?.let { return@withContext it }

        val file = File(cacheDir, "$key.$EXT")
        if (!file.exists()) return@withContext null

        file.setLastModified(System.currentTimeMillis())

        val opts = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        val bmp = BitmapFactory.decodeFile(file.absolutePath, opts)

        if (bmp != null) memory?.put(key, bmp)
        bmp
    }

    override suspend fun put(key: String, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val mutex = mutexByKey.getOrPut(key) { Mutex() }
        mutex.withLock { putNoLock(key, bitmap) }
    }

    override suspend fun getOrLoad(
        url: String,
        timeUs: Long,
        loader: suspend () -> Bitmap?
    ): Bitmap? = withContext(Dispatchers.IO) {
        val key = makeKey(url, timeUs)

        get(key)?.let { return@withContext it }

        val mutex = mutexByKey.getOrPut(key) { Mutex() }
        mutex.withLock {
            get(key)?.let { return@withLock it }

            val bmp = loader() ?: return@withLock null
            putNoLock(key, bmp)
            get(key)
        }
    }

    private fun putNoLock(key: String, bitmap: Bitmap) {
        memory?.get(key)?.let { return }

        val file = File(cacheDir, "$key.$EXT")
        if (file.exists()) {
            file.setLastModified(System.currentTimeMillis())
            BitmapFactory.decodeFile(file.absolutePath)?.let { memory?.put(key, it) }
            return
        }

        // Нормализация реально помогает от "зелени/полос"
        val normalized = bitmap.normalizeForSaving()
        val scaled = normalized.scaleDownToFit(maxW, maxH)

        runCatching {
            file.outputStream().use { out ->
                val format = chooseWebpFormat(webpLossless)
                val ok = scaled.compress(format, webpQuality, out)
                if (!ok) throw IllegalStateException("Bitmap.compress() returned false")
            }
            file.setLastModified(System.currentTimeMillis())
        }

        if (scaled !== normalized) runCatching { scaled.recycle() }
        runCatching { normalized.recycle() }

        trimToSize(maxDiskBytes)

        val opts = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        BitmapFactory.decodeFile(file.absolutePath, opts)?.let { memory?.put(key, it) }
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        memory?.evictAll()
        cacheDir.listFiles()?.forEach { it.delete() }
        mutexByKey.clear()
    }

    private fun trimToSize(maxBytes: Long) {
        val files = cacheDir.listFiles()?.filter { it.isFile } ?: return
        var total = files.sumOf { it.length() }
        if (total <= maxBytes) return

        val sorted = files.sortedBy { it.lastModified() }
        for (f in sorted) {
            if (total <= maxBytes) break
            val len = f.length()
            if (f.delete()) total -= len
        }
    }

    companion object {
        private const val DEFAULT_VIDEO_CACHE_MB = 150
        private const val MIN_VIDEO_CACHE_MB = 50
        private const val MAX_VIDEO_CACHE_MB = 500
    }
}

/** Нормализация: рисуем в “чистый” ARGB_8888 буфер */
private fun Bitmap.normalizeForSaving(): Bitmap {
    val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(out)
    canvas.drawBitmap(this, 0f, 0f, null)
    return out
}

private fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

private fun Bitmap.scaleDownToFit(maxW: Int, maxH: Int): Bitmap {
    val w = width
    val h = height
    if (w <= 0 || h <= 0) return this

    val scale = minOf(maxW.toFloat() / w, maxH.toFloat() / h, 1f)
    if (scale >= 1f) return this

    val nw = (w * scale).roundToInt().coerceAtLeast(1)
    val nh = (h * scale).roundToInt().coerceAtLeast(1)

    return Bitmap.createScaledBitmap(this, nw, nh, true)
}

private fun chooseWebpFormat(webpLossless: Boolean): Bitmap.CompressFormat {
    return when {
        Build.VERSION.SDK_INT >= 31 -> {
            if (webpLossless) Bitmap.CompressFormat.WEBP_LOSSLESS
            else Bitmap.CompressFormat.WEBP_LOSSY
        }

        else -> {
            // API 30 и ниже: используем старый WEBP
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
    }
}
