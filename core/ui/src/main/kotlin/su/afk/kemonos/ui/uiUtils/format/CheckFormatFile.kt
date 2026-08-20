package su.afk.kemonos.ui.uiUtils.format

import su.afk.kemonos.domain.MediaUrlScheme
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.utils.file.isImageFile as isImageFileShared
import su.afk.kemonos.utils.file.isVideoFile as isVideoFileShared

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

fun isVideoFile(path: String?): Boolean = isVideoFileShared(path)

fun isImageFile(path: String?): Boolean = isImageFileShared(path)

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

fun findFirstImagePath(post: PostDomain): String? {
    val filePath = post.file?.path
    if (isImageFile(filePath) && !isBinFile(filePath)) return filePath

    return post.attachments
        .asSequence()
        .map { it.path }
        .firstOrNull { isImageFile(it) && !isBinFile(it) }
}

fun findFirstVideoPath(post: PostDomain): String? {
    val filePath = post.file?.path
    if (isVideoFile(filePath)) return filePath

    return post.attachments
        .asSequence()
        .map { it.path }
        .firstOrNull { isVideoFile(it) }
}

/** Сборка URL файла по схеме источника. */
fun buildFileUrl(baseUrl: String, path: String, scheme: MediaUrlScheme): String =
    when (scheme) {
        MediaUrlScheme.DATA_PREFIXED -> "$baseUrl/data$path"
        MediaUrlScheme.DIRECT -> "$baseUrl$path"
    }

/** Аватар автора: у источников разные конвенции путей. */
fun buildCreatorAvatarUrl(
    imageBaseUrl: String,
    service: String,
    creatorId: String,
    scheme: MediaUrlScheme,
): String = when (scheme) {
    MediaUrlScheme.DATA_PREFIXED -> "$imageBaseUrl/icons/$service/$creatorId"
    MediaUrlScheme.DIRECT -> "$imageBaseUrl/creator/$service/$creatorId/avatar.webp"
}

/** Шапка профиля автора. */
fun buildCreatorBannerUrl(
    imageBaseUrl: String,
    service: String,
    creatorId: String,
    scheme: MediaUrlScheme,
): String = when (scheme) {
    MediaUrlScheme.DATA_PREFIXED -> "$imageBaseUrl/banners/$service/$creatorId"
    MediaUrlScheme.DIRECT -> "$imageBaseUrl/creator/$service/$creatorId/header.webp"
}

/**
 * Сборка URL превью по схеме источника.
 *
 * [thumbnailPath] — готовый путь, если источник задаёт его явно (OnlyHaven).
 * [segment] — сегмент kemono-схемы: обычно "thumbnail", но у превью поста это `preview.type`.
 */
fun buildThumbnailUrl(
    imageBaseUrl: String,
    path: String,
    scheme: MediaUrlScheme,
    thumbnailPath: String? = null,
    segment: String = "thumbnail",
): String = when {
    thumbnailPath != null -> "$imageBaseUrl$thumbnailPath"
    scheme == MediaUrlScheme.DATA_PREFIXED -> "$imageBaseUrl/$segment/data$path"
    else -> "$imageBaseUrl$path"
}

fun buildVideoPreviewUrl(
    videoPath: String?,
    enabled: Boolean,
    previewServerUrl: String,
): String? {
    if (!enabled) return null

    val normalizedPath = videoPath
        ?.substringBefore('?')
        ?.substringBefore('#')
        ?.takeIf { it.isNotBlank() }
        ?.removeSuffix("/")
        ?: return null

    val extIndex = normalizedPath.lastIndexOf('.')
    if (extIndex <= 0) return null

    val previewPath = normalizedPath.substring(0, extIndex)
    return "${previewServerUrl.trimEnd('/')}/thumbnail/${previewPath.trimStart('/')}/25.webp"
}

fun buildVideoPreviewUrls(
    videoPath: String?,
    enabled: Boolean,
    previewServerUrl: String,
): List<String> {
    val previewUrl25 = buildVideoPreviewUrl(
        videoPath = videoPath,
        enabled = enabled,
        previewServerUrl = previewServerUrl,
    ) ?: return emptyList()
    val previewBaseUrl = previewUrl25.removeSuffix("/25.webp")

    return listOf(
        "$previewBaseUrl/25.webp",
        "$previewBaseUrl/50.webp",
        "$previewBaseUrl/75.webp",
    )
}
