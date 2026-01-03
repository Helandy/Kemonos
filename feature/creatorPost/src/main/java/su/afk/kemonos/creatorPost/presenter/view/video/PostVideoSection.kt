package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.common.R
import su.afk.kemonos.creatorPost.domain.model.video.VideoInfoState
import su.afk.kemonos.creatorPost.presenter.view.VideoInfoPreview
import su.afk.kemonos.domain.models.VideoDomain

internal fun LazyListScope.postVideosSection(
    videos: List<VideoDomain>,
    observeVideoInfo: (String, String) -> StateFlow<VideoInfoState>,
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
        VideoInfoPreview(
            video = videos[index],
            observeVideoInfo = observeVideoInfo
        )
    }
}