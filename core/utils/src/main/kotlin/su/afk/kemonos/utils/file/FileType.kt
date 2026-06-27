package su.afk.kemonos.utils.file

private val VIDEO_EXTENSIONS = setOf(
    "mp4", "m4v", "mov", "webm", "avi",
    "wmv", "flv", "mpeg", "mpg", "3gp", "mkv"
)

private val IMAGE_EXTENSIONS = setOf(
    "jpg", "jpeg", "png", "webp", "gif", "bmp", "heic", "heif", "avif"
)

fun String.extensionOrNull(): String? =
    substringAfterLast('/', this)
        .substringBefore('?')
        .substringBefore('#')
        .substringAfterLast('.', missingDelimiterValue = "")
        .takeIf { it.isNotBlank() }

fun isVideoFile(path: String?): Boolean =
    path?.extensionOrNull()?.lowercase() in VIDEO_EXTENSIONS

fun isImageFile(path: String?): Boolean =
    path?.extensionOrNull()?.lowercase() in IMAGE_EXTENSIONS
