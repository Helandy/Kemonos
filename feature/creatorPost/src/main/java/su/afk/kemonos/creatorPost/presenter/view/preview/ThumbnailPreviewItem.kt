package su.afk.kemonos.creatorPost.presenter.view.preview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.views.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.domain.domain.models.PreviewDomain
import java.net.URLEncoder

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
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(4.dp))
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
                    if (ratio == null || kotlin.math.abs(ratio!! - newRatio) > 0.01f) {
                        ratio = newRatio
                    }
                }
            }
        )

        FilledIconButton(
            onClick = { onDownloadClick(urls.fullUrl, filename) },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 8.dp)
                .size(46.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = stringResource(R.string.download),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
