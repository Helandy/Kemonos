package su.afk.kemonos.creatorPost.presenter.view.preview

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import su.afk.kemonos.domain.models.PreviewDomain

internal fun LazyListScope.postPreviewsSection(
    previews: List<PreviewDomain>,
    imgBaseUrl: String,
    onOpenImage: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    downloadStarted: String,
    downloadStartedNamed: String,
    download: (url: String, fileName: String) -> Unit,
    toast: (String) -> Unit,
) {
    if (previews.isEmpty()) return

    items(
        items = previews,
        key = { p -> p.previewKey() }
    ) { preview ->
        when (preview.type) {
            "thumbnail" -> ThumbnailPreviewItem(
                preview = preview,
                imgBaseUrl = imgBaseUrl,
                onPreviewClick = onOpenImage,
                onDownloadClick = { fullUrl, fileName ->
                    download(fullUrl, fileName)

                    val msg = if (fileName.isBlank()) downloadStarted
                    else String.format(downloadStartedNamed, fileName)

                    toast(msg)
                }
            )

            "embed" -> EmbedPreviewItem(
                preview = preview,
                onEmbedClick = onOpenUrl
            )
        }
    }
}

/** единая логика ключа */
internal fun PreviewDomain.previewKey(): String = when (type) {
    "thumbnail" -> "t:${path}"
    "embed" -> "e:${url}"
    else -> "${type}:${path}:${url}"
}