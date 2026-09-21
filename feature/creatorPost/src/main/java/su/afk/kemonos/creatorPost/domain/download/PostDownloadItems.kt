package su.afk.kemonos.creatorPost.domain.download

import su.afk.kemonos.ui.uiUtils.format.buildFileUrl
import su.afk.kemonos.domain.MediaUrlScheme
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.domain.models.VideoDomain
import su.afk.kemonos.preferences.ui.DownloadFileNameMode
import su.afk.kemonos.ui.uiUtils.format.isAudioFile
import su.afk.kemonos.utils.download.isHashLikeFileName
import su.afk.kemonos.utils.download.numberedFileName
import su.afk.kemonos.utils.url.buildContentUrl
import java.net.URLEncoder
import java.util.*

internal data class PostDownloadItem(
    val url: String,
    val fileName: String?,
)

/**
 * Все файлы поста в том порядке, в котором они идут на странице.
 *
 * [includeCover] — брать ли обложку поста (`post.file`); [fileNameMode] решает,
 * оставить имя источника или заменить его на порядковый номер страницы.
 */
internal fun PostContentDomain.collectDownloadAllItems(
    fallbackBaseUrl: String,
    mediaUrlScheme: MediaUrlScheme,
    fileNameMode: DownloadFileNameMode = DownloadFileNameMode.ORIGINAL,
    includeCover: Boolean = true,
): List<PostDownloadItem> {
    val coverPath = post.file?.path.normalizeCoverPath()

    val items = buildList {
        previews.asSequence()
            .distinctBy { it.previewKey() }
            .mapNotNull { it.toThumbnailDownloadItemOrNull(mediaUrlScheme) }
            .forEach(::add)

        videos.asSequence()
            .distinctBy { video -> "video:${video.server}:${video.path}" }
            .map { it.toDownloadItem(mediaUrlScheme) }
            .forEach(::add)

        attachments.asSequence()
            .filter { isAudioFile(it.path) }
            .distinctBy { "${it.server.orEmpty()}|${it.path}" }
            .map { it.toAttachmentDownloadItem(fallbackBaseUrl, mediaUrlScheme) }
            .forEach(::add)

        attachments.asSequence()
            .map { it.toAttachmentDownloadItem(fallbackBaseUrl, mediaUrlScheme) }
            .forEach(::add)
    }
        .distinctBy(PostDownloadItem::dedupKey)
        .filter { it.url.isNotBlank() }

    val withoutCover = if (includeCover || coverPath == null) {
        items
    } else {
        items.filterNot { it.matchesPath(coverPath) }
    }

    return withoutCover.withNumberedNames(fileNameMode)
}

private fun PreviewDomain.previewKey(): String = when (type) {
    "thumbnail" -> "t:${path}"
    "embed" -> "e:${url}"
    else -> "${type}:${path}:${url}"
}

private fun PreviewDomain.toThumbnailDownloadItemOrNull(scheme: MediaUrlScheme): PostDownloadItem? {
    if (type != "thumbnail") return null

    val server = server ?: return null
    val path = path ?: return null
    val name = name ?: return null
    val encodedName = URLEncoder.encode(name, Charsets.UTF_8.name())

    return PostDownloadItem(
        url = buildFileUrl(server, path, scheme) + "?f=" + encodedName,
        fileName = name
    )
}

private fun VideoDomain.toDownloadItem(scheme: MediaUrlScheme): PostDownloadItem = PostDownloadItem(
    url = buildFileUrl(server.orEmpty(), path, scheme),
    fileName = name
)

private fun AttachmentDomain.toAttachmentDownloadItem(
    fallbackBaseUrl: String,
    mediaUrlScheme: MediaUrlScheme,
): PostDownloadItem = PostDownloadItem(
    url = buildContentUrl(mediaUrlScheme, fallbackBaseUrl),
    fileName = name
)

private fun PostDownloadItem.dedupKey(): String {
    val source = url.trim()
    if (source.isBlank()) return source

    val withoutParams = source.substringBefore('#').substringBefore('?')

    val afterData = withoutParams.substringAfter("/data/", missingDelimiterValue = "")
        .ifBlank { withoutParams.substringAfter("data/", missingDelimiterValue = "") }
        .takeIf { it.isNotBlank() }
        ?.removePrefix("/")
        ?.lowercase(Locale.ROOT)

    return afterData ?: withoutParams.lowercase(Locale.ROOT)
}

/** Ссылка без query: по ней сопоставляем файл поста с тем, что нажали в UI. */
internal fun String.downloadUrlKey(): String =
    substringBefore('#').substringBefore('?').lowercase(Locale.ROOT)

/** Обложка — это `post.file`, своего url у неё нет, поэтому сверяем путь файла. */
private fun String?.normalizeCoverPath(): String? = this
    ?.trim()
    ?.removePrefix("/")
    ?.lowercase(Locale.ROOT)
    ?.takeIf { it.isNotBlank() }

private fun PostDownloadItem.matchesPath(path: String): Boolean =
    url.downloadUrlKey().endsWith(path)

/**
 * Заменяет имена файлов на порядковые номера: `1.png`, `2.webp`.
 *
 * Счётчик растёт только на переименованных файлах, поэтому в режиме
 * [DownloadFileNameMode.NUMBERED_FOR_HASH_NAMES] нумерация остаётся сплошной,
 * даже если часть файлов сохранила осмысленные имена источника.
 */
private fun List<PostDownloadItem>.withNumberedNames(
    mode: DownloadFileNameMode,
): List<PostDownloadItem> {
    if (mode == DownloadFileNameMode.ORIGINAL) return this

    var number = 0
    return map { item ->
        val sourceName = item.fileName?.takeIf { it.isNotBlank() }

        val keepSourceName = mode == DownloadFileNameMode.NUMBERED_FOR_HASH_NAMES &&
                sourceName != null &&
                !isHashLikeFileName(sourceName)

        if (keepSourceName) return@map item

        number++
        item.copy(
            fileName = numberedFileName(
                number = number,
                sourceName = sourceName,
                sourceUrl = item.url,
            )
        )
    }
}
