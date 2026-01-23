package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.domain.models.VideoDomain

internal fun LazyListScope.postVideosSection(
    videos: List<VideoDomain>,
    videoInfo: Map<String, MediaInfoState>,
    onVideoInfoRequested: (url: String) -> Unit,
    onDownload: (url: String, fileName: String) -> Unit,
) {
    val uniqueVideos = videos.distinctBy { v ->
        "video:${v.server}:${v.path}"
    }
    if (uniqueVideos.isEmpty()) return

    item(key = "videos_header") {
        Text(
            text = stringResource(R.string.video_section),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )
    }

    items(
        count = uniqueVideos.size,
        key = { index ->
            val v = uniqueVideos[index]
            "video:${v.server}:${v.path}"
        }
    ) { index ->
        val video = uniqueVideos[index]
        VideoInfoPreview(
            video = video,
            infoState = videoInfo["${video.server}/data${video.path}"],
            requestInfo = onVideoInfoRequested,
            onDownloadClick = onDownload
        )
    }
}