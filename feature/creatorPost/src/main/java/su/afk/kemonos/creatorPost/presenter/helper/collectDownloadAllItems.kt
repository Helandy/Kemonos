package su.afk.kemonos.creatorPost.presenter.helper

import su.afk.kemonos.ui.uiUtils.format.buildFileUrl
import su.afk.kemonos.domain.MediaUrlScheme
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.domain.models.VideoDomain
import su.afk.kemonos.ui.uiUtils.format.isAudioFile
import su.afk.kemonos.utils.url.buildContentUrl
import java.net.URLEncoder
import java.util.*

internal data class PostDownloadItem(
    val url: String,
    val fileName: String?,
)

internal fun PostContentDomain.collectDownloadAllItems(
    fallbackBaseUrl: String,
    mediaUrlScheme: MediaUrlScheme,
): List<PostDownloadItem> = buildList {
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
