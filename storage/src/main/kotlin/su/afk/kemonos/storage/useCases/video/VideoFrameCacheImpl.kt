package su.afk.kemonos.storage.useCases.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import su.afk.kemonos.storage.api.video.IVideoFrameCache
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.math.roundToInt

class VideoFrameCacheImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : IVideoFrameCache {

    private val maxDiskBytes: Long = 150L * 1024L * 1024L // 150MB
    private val useMemory: Boolean = true
    private val maxMemoryBytes: Int = 48 * 1024 * 1024 // 48MB
    private val maxW: Int = 1280
    private val maxH: Int = 720
    private val webpQuality: Int = 75
    private val jpegQuality: Int = 80

    private val cacheDir: File = File(appContext.cacheDir, "video_frames").apply { mkdirs() }

    /** mutex per key — защита от гонок записи одного и того же файла */
    private val mutexByKey = ConcurrentHashMap<String, Mutex>()

    private val memory: LruCache<String, Bitmap>? = if (useMemory) {
        object : LruCache<String, Bitmap>(maxMemoryBytes) {
            override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount
        }
    } else null

    override fun makeKey(url: String, timeUs: Long): String {
        val raw = "$url|t=$timeUs|max=${maxW}x$maxH|q=$webpQuality"
        return raw.sha256()
    }

    override suspend fun get(key: String): Bitmap? = withContext(Dispatchers.IO) {
        // 1) memory
        memory?.get(key)?.let { return@withContext it }

        // 2) disk
        val file = File(cacheDir, "$key.webp")
        if (!file.exists()) return@withContext null

        // touch для LRU
        file.setLastModified(System.currentTimeMillis())

        val bmp = BitmapFactory.decodeFile(file.absolutePath)
        if (bmp != null) memory?.put(key, bmp)
        bmp
    }

    override suspend fun put(key: String, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val mutex = mutexByKey.getOrPut(key) { Mutex() }
        mutex.withLock {
            putNoLock(key, bitmap)
        }
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

            // ВАЖНО: без повторного lock
            putNoLock(key, bmp)

            get(key)
        }
    }

    private fun putNoLock(key: String, bitmap: Bitmap) {
        memory?.get(key)?.let { return }

        val file = File(cacheDir, "$key.jpg")
        if (file.exists()) {
            file.setLastModified(System.currentTimeMillis())
            BitmapFactory.decodeFile(file.absolutePath)?.let { memory?.put(key, it) }
            return
        }

        val scaled = bitmap
            .ensureArgb8888()          // важно для “зелени”/битых каналов
            .scaleDownToFit(maxW, maxH)

        runCatching {
            file.outputStream().use { out ->
                scaled.compress(Bitmap.CompressFormat.JPEG, jpegQuality, out)
            }
            file.setLastModified(System.currentTimeMillis())
        }

        // если сделали промежуточные bitmap — освободим
        if (scaled !== bitmap) runCatching { scaled.recycle() }

        trimToSize(maxDiskBytes)
        BitmapFactory.decodeFile(file.absolutePath)?.let { memory?.put(key, it) }
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

        // удаляем старые первыми
        val sorted = files.sortedBy { it.lastModified() }
        for (f in sorted) {
            if (total <= maxBytes) break
            val len = f.length()
            if (f.delete()) total -= len
        }
    }
}

/** Привести bitmap к ARGB_8888 (часто лечит странные цвета после decode/encode) */
private fun Bitmap.ensureArgb8888(): Bitmap {
    if (config == Bitmap.Config.ARGB_8888) return this
    return copy(Bitmap.Config.ARGB_8888, false)
}

/** SHA-256 для ключей */
private fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

/** Ресайзим, чтобы влезло в maxW x maxH, сохраняя пропорции */
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