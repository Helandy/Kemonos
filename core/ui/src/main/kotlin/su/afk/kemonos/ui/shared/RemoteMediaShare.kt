package su.afk.kemonos.ui.shared

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val SHARED_MEDIA_DIR = "shared_media"
private const val STALE_MS = 24L * 60L * 60L * 1000L
private const val CONNECT_TIMEOUT_SEC = 20L
private const val READ_TIMEOUT_SEC = 60L

private val shareHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
        .build()
}

suspend fun shareRemoteMedia(
    context: Context,
    url: String,
    fileName: String?,
    mime: String = "*/*",
): Boolean {
    val prepared = withContext(Dispatchers.IO) {
        cleanupSharedMediaCache(context)
        prepareSharedFile(context, url, fileName, mime)
    } ?: return false

    return withContext(Dispatchers.Main) {
        runCatching {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = prepared.mime
                putExtra(Intent.EXTRA_STREAM, prepared.uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newUri(context.contentResolver, prepared.file.name, prepared.uri)
            }

            val chooser = Intent.createChooser(shareIntent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        }.getOrDefault(false)
    }
}

suspend fun openRemoteAudioInExternalApp(
    context: Context,
    url: String,
    fileName: String?,
    mime: String = "audio/*",
): Boolean {
    val prepared = withContext(Dispatchers.IO) {
        cleanupSharedMediaCache(context)
        prepareSharedFile(context, url, fileName, mime)
    } ?: return false

    return withContext(Dispatchers.Main) {
        runCatching {
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(prepared.uri, prepared.mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                clipData = ClipData.newUri(context.contentResolver, prepared.file.name, prepared.uri)
                putExtra(Intent.EXTRA_TITLE, prepared.file.name)
            }
            val chooser = Intent.createChooser(viewIntent, prepared.file.name).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        }.getOrDefault(false)
    }
}

private data class PreparedSharedMedia(
    val file: File,
    val uri: android.net.Uri,
    val mime: String,
)

private fun prepareSharedFile(
    context: Context,
    url: String,
    fileName: String?,
    mime: String,
): PreparedSharedMedia? {
    val resolvedName = resolveFileName(url, fileName, mime)
    val targetDir = File(context.cacheDir, SHARED_MEDIA_DIR).apply { mkdirs() }
    val targetFile = File(targetDir, resolvedName)

    if (!downloadToFile(url, targetFile)) return null

    val authority = "${context.packageName}.fileprovider"
    val uri = runCatching {
        FileProvider.getUriForFile(context, authority, targetFile)
    }.getOrNull() ?: return null

    val resolvedMime = resolveMimeType(resolvedName, mime)
    return PreparedSharedMedia(file = targetFile, uri = uri, mime = resolvedMime)
}

private fun downloadToFile(url: String, targetFile: File): Boolean {
    val request = Request.Builder().url(url).build()
    val tmp = File(targetFile.parentFile, "${targetFile.name}.tmp")

    return runCatching {
        shareHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return false
            val body = response.body
            tmp.outputStream().use { out ->
                body.byteStream().use { input -> input.copyTo(out) }
            }
        }

        if (targetFile.exists() && !targetFile.delete()) {
            throw IOException("Failed to replace old file ${targetFile.absolutePath}")
        }
        if (!tmp.renameTo(targetFile)) {
            throw IOException("Failed to move tmp file to ${targetFile.absolutePath}")
        }
        true
    }.getOrElse {
        runCatching { tmp.delete() }
        false
    }
}

fun cleanupSharedMediaCache(context: Context, nowMs: Long = System.currentTimeMillis()) {
    val dir = File(context.cacheDir, SHARED_MEDIA_DIR)
    if (!dir.exists()) return

    dir.listFiles().orEmpty().forEach { file ->
        if (!file.isFile) return@forEach
        val age = nowMs - file.lastModified()
        if (age >= STALE_MS) runCatching { file.delete() }
    }
}

private fun resolveFileName(url: String, fileName: String?, mime: String): String {
    val raw = fileName?.takeIf { it.isNotBlank() }
        ?: url.substringAfterLast('/').substringBefore('?').substringBefore('#').takeIf { it.isNotBlank() }
        ?: "shared_file"

    val safe = raw
        .replace(Regex("""[\\/:*?"<>|]"""), "_")
        .replace("\n", " ")
        .replace("\r", " ")
        .trim()
        .take(120)
        .ifBlank { "shared_file" }

    if (safe.contains('.')) return safe

    val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
    return if (ext.isNullOrBlank()) safe else "$safe.$ext"
}

private fun resolveMimeType(fileName: String, fallbackMime: String): String {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    if (ext.isNotBlank()) {
        val fromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        if (!fromExt.isNullOrBlank()) return fromExt
    }
    return fallbackMime
}
