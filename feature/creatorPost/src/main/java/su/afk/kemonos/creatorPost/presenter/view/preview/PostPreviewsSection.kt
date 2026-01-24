package su.afk.kemonos.creatorPost.presenter.view.preview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.models.PreviewDomain

internal fun LazyListScope.postPreviewsSection(
    previews: List<PreviewDomain>,
    imgBaseUrl: String,
    showNames: Boolean,
    onTogglePreviewNames: () -> Unit,
    onOpenImage: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    download: (url: String, fileName: String) -> Unit,
) {
    val uniquePreviews = previews.distinctBy { it.previewKey() }
    if (previews.isEmpty()) return

    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = if (showNames)
                    Icons.AutoMirrored.Outlined.TextSnippet
                else
                    Icons.AutoMirrored.Outlined.Label,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(26.dp)
                    .clickable { onTogglePreviewNames() }
            )
        }
    }

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
                onDownloadClick = download
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