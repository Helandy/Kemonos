package su.afk.kemonos.creatorPost.presenter.view.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.domain.models.PreviewDomain
import java.net.URLEncoder
import kotlin.math.abs

private data class PreviewUrls(
    val thumbUrl: String,
    val fullUrl: String,
)

private fun buildPreviewUrls(imgBaseUrl: String, preview: PreviewDomain): PreviewUrls? {
    val server = preview.server ?: return null
    val path = preview.path ?: return null
    val name = preview.name ?: return null

    val thumbUrl = "$imgBaseUrl/${preview.type}/data$path"
    val fullUrl = "$server/data$path?f=" + URLEncoder.encode(name, "UTF-8")
    return PreviewUrls(thumbUrl = thumbUrl, fullUrl = fullUrl)
}

@Composable
fun ThumbnailPreviewItem(
    preview: PreviewDomain,
    imgBaseUrl: String,
    showFileName: Boolean,
    onPreviewClick: (String) -> Unit,
    onDownloadClick: (String, String) -> Unit,
) {
    val urls = remember(preview, imgBaseUrl) { buildPreviewUrls(imgBaseUrl, preview) } ?: return
    val filename = preview.name.orEmpty()

    var ratio by rememberSaveable(urls.thumbUrl) { mutableStateOf<Float?>(null) }

    /** временный, пока не знаем размер картинки */
    val safeRatio = ratio ?: 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(6.dp))
    ) {
        AsyncImageWithStatus(
            model = urls.thumbUrl,
            contentDescription = preview.name,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(safeRatio)
                .clickable { onPreviewClick(urls.fullUrl) },
            onSuccessSize = { size: Size ->
                val w = size.width
                val h = size.height
                if (w.isFinite() && h.isFinite() && w > 0f && h > 0f) {
                    val newRatio = w / h
                    if (ratio == null || abs(ratio!! - newRatio) > 0.01f) {
                        ratio = newRatio
                    }
                }
            }
        )

        /** ⬇️ Имя файла (overlay) */
        androidx.compose.animation.AnimatedVisibility(
            visible = showFileName && filename.isNotBlank(),
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = filename,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        /** ⬇️ Download */
        FilledIconButton(
            onClick = { onDownloadClick(urls.fullUrl, filename) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(42.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = stringResource(R.string.download),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
