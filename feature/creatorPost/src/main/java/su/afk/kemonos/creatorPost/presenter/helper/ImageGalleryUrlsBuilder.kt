package su.afk.kemonos.creatorPost.presenter.helper

import su.afk.kemonos.domain.MediaUrlScheme
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.mediaUrlSchemeByService
import su.afk.kemonos.ui.presenter.androidView.model.PostBlock
import su.afk.kemonos.ui.uiUtils.format.buildFileUrl
import su.afk.kemonos.ui.uiUtils.format.buildThumbnailUrl
import java.net.URLEncoder
import javax.inject.Inject

/** Собирает список URL картинок поста (+ thumbnail) для галереи image-view. */
internal class ImageGalleryUrlsBuilder @Inject constructor(
    private val domainResolver: IDomainResolver,
) {
    /** @return пары (полный URL, thumbnail URL); выбранный URL всегда присутствует в списке. */
    fun build(
        service: String,
        contentBlocks: List<PostBlock>?,
        previews: List<PreviewDomain>?,
        selectedUrl: String,
    ): List<Pair<String, String?>> {
        val imgBaseUrl = domainResolver.imageBaseUrlByService(service)
        val scheme = domainResolver.mediaUrlSchemeByService(service)

        val contentImages = contentBlocks
            .orEmpty()
            .mapNotNull { block -> (block as? PostBlock.Image)?.url }
            .filter { it.isNotBlank() }
            .map { it to null }

        val previewImages = previews
            .orEmpty()
            .asSequence()
            .filter { it.type == "thumbnail" }
            .mapNotNull { preview ->
                val fullUrl = buildPreviewFullUrl(preview, scheme)
                val thumbnailUrl = buildPreviewThumbnailUrl(imgBaseUrl, preview, scheme)
                if (fullUrl != null) fullUrl to thumbnailUrl else null
            }
            .toList()

        val mergedMap = linkedMapOf<String, String?>()
        (contentImages + previewImages).forEach { (fullUrl, thumbnailUrl) ->
            when {
                !mergedMap.containsKey(fullUrl) -> mergedMap[fullUrl] = thumbnailUrl
                mergedMap[fullUrl].isNullOrBlank() && !thumbnailUrl.isNullOrBlank() -> {
                    mergedMap[fullUrl] = thumbnailUrl
                }
            }
        }

        val merged = mergedMap.entries.map { it.key to it.value }
        return if (selectedUrl in merged.map { it.first }) {
            merged
        } else {
            val selectedPair = listOf(selectedUrl to null)
            (selectedPair + merged).distinctBy { it.first }
        }
    }

    /** Строит полный URL для thumbnail-превью */
    private fun buildPreviewFullUrl(preview: PreviewDomain, scheme: MediaUrlScheme): String? {
        val server = preview.server ?: return null
        val path = preview.path ?: return null
        val name = preview.name ?: return null

        return buildFileUrl(server, path, scheme) + "?f=" + URLEncoder.encode(name, "UTF-8")
    }

    /** Строит thumbnail URL из PreviewDomain */
    private fun buildPreviewThumbnailUrl(imgBaseUrl: String, preview: PreviewDomain, scheme: MediaUrlScheme): String? {
        val path = preview.path ?: return null
        return buildThumbnailUrl(
            imageBaseUrl = imgBaseUrl,
            path = path,
            scheme = scheme,
            thumbnailPath = preview.thumbnailPath,
        )
    }
}
