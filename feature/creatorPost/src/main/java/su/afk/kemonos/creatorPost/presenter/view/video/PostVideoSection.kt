package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.creatorPost.domain.model.video.VideoInfoState
import su.afk.kemonos.creatorPost.presenter.view.VideoInfoPreview
import su.afk.kemonos.domain.models.VideoDomain

internal fun LazyListScope.postVideosSection(
    videos: List<VideoDomain>,
    videoInfo: Map<String, VideoInfoState>,
    onVideoInfoRequested: (url: String, name: String) -> Unit,
) {
    if (videos.isEmpty()) return

    item(key = "videos_header") {
        Text(
            text = stringResource(R.string.video_section),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }

    items(
        count = videos.size,
        key = { index ->
            val v = videos[index]
            "video:${v.server}:${v.name}:${v.path}"
        }
    ) { index ->
        val video = videos[index]
        VideoInfoPreview(
            video = video,
            infoState = videoInfo["${video.server}/data${video.path}"],
            requestInfo = onVideoInfoRequested
        )
    }
}