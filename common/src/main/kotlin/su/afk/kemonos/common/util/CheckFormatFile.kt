package su.afk.kemonos.common.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import su.afk.kemonos.domain.models.PostDomain

private val AUDIO_EXTENSIONS = setOf(
    "mp3", "m4a", "aac", "wav", "ogg", "flac", "opus", "wma"
)

fun isAudioFile(path: String?): Boolean {
    if (path.isNullOrBlank()) return false
    val ext = path.substringAfterLast('.', "")
    return ext.lowercase() in AUDIO_EXTENSIONS
}

fun audioMimeType(path: String?): String = when (path?.substringAfterLast('.', "")?.lowercase()) {
    "mp3" -> "audio/mpeg"
    "m4a" -> "audio/mp4"
    "aac" -> "audio/aac"
    "wav" -> "audio/wav"
    "ogg" -> "audio/ogg"
    "flac" -> "audio/flac"
    "opus" -> "audio/opus"
    else -> "audio/*"
}

fun openAudioExternally(context: Context, url: String, fileName: String? = null, mime: String = "audio/*") {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(url.toUri(), mime)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_TITLE, fileName)
    }
    context.startActivity(intent)
}

fun isBinFile(path: String?): Boolean {
    if (path == null) return false
    return path.endsWith(".bin", ignoreCase = true)
}

private val VIDEO_EXTENSIONS = setOf(
    "mp4", "m4v", "mov", "webm", "avi",
    "wmv", "flv", "mpeg", "mpg", "3gp", "mkv"
)

fun isVideoFile(path: String?): Boolean {
    if (path.isNullOrBlank()) return false
    val ext = path.substringAfterLast('.', "")
    return ext.lowercase() in VIDEO_EXTENSIONS
}

fun countVideoFiles(post: PostDomain): Int {
    var count = 0

    /** основной файл поста */
    if (isVideoFile(post.file?.path)) {
        count++
    }

    /** вложения */
    count += post.attachments.count { attachment ->
        isVideoFile(attachment.path)
    }

    return count
}

private val IMAGE_EXTENSIONS = setOf(
    "jpg", "jpeg", "png", "webp", "gif", "bmp", "heic", "heif", "avif"
)

fun isImageFile(path: String?): Boolean {
    if (path.isNullOrBlank()) return false
    val ext = path.substringAfterLast('.', "")
    return ext.isNotBlank() && ext.lowercase() in IMAGE_EXTENSIONS
}

fun findFirstImagePath(post: PostDomain): String? {
    val filePath = post.file?.path
    if (isImageFile(filePath) && !isBinFile(filePath)) return filePath

    return post.attachments
        .asSequence()
        .map { it.path }
        .firstOrNull { isImageFile(it) && !isBinFile(it) }
}