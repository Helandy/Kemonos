package su.afk.kemonos.common.util

import su.afk.kemonos.domain.models.PostDomain

fun isAudioFile(path: String?): Boolean {
    if (path == null) return false
    return path.endsWith(".mp3", ignoreCase = true)
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