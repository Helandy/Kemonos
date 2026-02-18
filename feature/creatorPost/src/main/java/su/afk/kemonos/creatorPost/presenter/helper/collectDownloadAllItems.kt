package su.afk.kemonos.creatorPost.presenter.helper

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.domain.models.VideoDomain
import su.afk.kemonos.ui.uiUtils.format.isAudioFile
import su.afk.kemonos.utils.url.buildContentUrlToDataSite
import java.net.URLEncoder

internal data class PostDownloadItem(
    val url: String,
    val fileName: String?,
)

internal fun PostContentDomain.collectDownloadAllItems(fallbackBaseUrl: String): List<PostDownloadItem> = buildList {
    previews.asSequence()
        .distinctBy { it.previewKey() }
        .mapNotNull { it.toThumbnailDownloadItemOrNull() }
        .forEach(::add)

    videos.asSequence()
        .distinctBy { video -> "video:${video.server}:${video.path}" }
        .map(VideoDomain::toDownloadItem)
        .forEach(::add)

    attachments.asSequence()
        .filter { isAudioFile(it.path) }
        .distinctBy { "${it.server.orEmpty()}|${it.path}" }
        .map { it.toAttachmentDownloadItem(fallbackBaseUrl) }
        .forEach(::add)

    attachments.asSequence()
        .map { it.toAttachmentDownloadItem(fallbackBaseUrl) }
        .forEach(::add)
}
    .distinctBy { item -> item.url.substringBefore('?').trim() }
    .filter { it.url.isNotBlank() }

private fun PreviewDomain.previewKey(): String = when (type) {
    "thumbnail" -> "t:${path}"
    "embed" -> "e:${url}"
    else -> "${type}:${path}:${url}"
}

private fun PreviewDomain.toThumbnailDownloadItemOrNull(): PostDownloadItem? {
    if (type != "thumbnail") return null

    val server = server ?: return null
    val path = path ?: return null
    val name = name ?: return null
    val encodedName = URLEncoder.encode(name, Charsets.UTF_8.name())

    return PostDownloadItem(
        url = "$server/data$path?f=$encodedName",
        fileName = name
    )
}

private fun VideoDomain.toDownloadItem(): PostDownloadItem = PostDownloadItem(
    url = "${server}/data${path}",
    fileName = name
)

private fun AttachmentDomain.toAttachmentDownloadItem(fallbackBaseUrl: String): PostDownloadItem = PostDownloadItem(
    url = buildContentUrlToDataSite(fallbackBaseUrl),
    fileName = name
)
