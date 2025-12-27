package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.common.R
import su.afk.kemonos.creatorPost.domain.model.video.VideoInfoState
import su.afk.kemonos.creatorPost.presenter.view.VideoInfoPreview
import su.afk.kemonos.domain.domain.models.VideoDomain

@Composable
fun PostVideosSection(
    videos: List<VideoDomain>,
    observeVideoInfo: (String, String) -> StateFlow<VideoInfoState>,
) {
    if (videos.isEmpty()) return

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            stringResource(R.string.video_section),
            style = MaterialTheme.typography.titleMedium
        )

        videos.forEach { video ->
            VideoInfoPreview(
                video = video,
                observeVideoInfo = observeVideoInfo,
            )
        }
    }
}
