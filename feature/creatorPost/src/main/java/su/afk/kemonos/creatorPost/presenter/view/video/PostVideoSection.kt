package su.afk.kemonos.creatorPost.presenter.view.video

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.creatorPost.domain.model.video.VideoThumbState
import su.afk.kemonos.domain.models.VideoDomain
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.R

internal fun LazyListScope.postVideosSection(
    uiSettingModel: UiSettingModel,
    videos: List<VideoDomain>,
    videoInfo: Map<String, MediaInfoState>,
    onVideoInfoRequested: (server: String, path: String) -> Unit,
    videoThumbs: Map<String, VideoThumbState>,
    requestThumb: (server: String, path: String) -> Unit,
    onDownload: (url: String, fileName: String) -> Unit,
    showHeader: Boolean = true,
) {
    if (videos.isEmpty()) return

    if (showHeader) {
        item(key = "videos_header") {
            Text(
                text = stringResource(R.string.video_section),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }
    }

    items(
        count = videos.size,
        key = { index -> "video:${videos[index].server}:${videos[index].path}" }
    ) { index ->
        val video = videos[index]
        val url = "${video.server}/data${video.path}"

        VideoPreviewItem(
            showPreview = uiSettingModel.showPreviewVideo,
            blurImage = uiSettingModel.blurImages,
            video = video,
            infoState = videoInfo[url],
            requestInfo = onVideoInfoRequested,
            thumbState = videoThumbs[url] ?: VideoThumbState.Idle,
            requestThumb = requestThumb,
            onDownloadClick = onDownload
        )
    }
}
