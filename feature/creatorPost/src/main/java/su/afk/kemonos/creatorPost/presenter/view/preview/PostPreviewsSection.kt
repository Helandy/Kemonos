package su.afk.kemonos.creatorPost.presenter.view.preview

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.preferences.ui.UiSettingModel

internal fun LazyListScope.postPreviewsSection(
    uiSettingModel: UiSettingModel,
    previews: List<PreviewDomain>,
    imgBaseUrl: String,
    showNames: Boolean,
    onOpenImage: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    download: (url: String, fileName: String) -> Unit,
) {
    val uniquePreviews = previews.distinctBy { it.previewKey() }
    if (previews.isEmpty()) return

    items(
        items = uniquePreviews,
        key = { p -> p.previewKey() }
    ) { preview ->
        when (preview.type) {
            "thumbnail" -> ThumbnailPreviewItem(
                preview = preview,
                imgBaseUrl = imgBaseUrl,
                showFileName = showNames,
                onPreviewClick = onOpenImage,
                onDownloadClick = download,
                blurImage = uiSettingModel.blurImages
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
