package su.afk.kemonos.ui.components.posts.postCard.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.posts.postCard.model.PreviewState
import su.afk.kemonos.ui.components.posts.postCard.placeHolder.PreviewPlaceholder
import su.afk.kemonos.ui.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.ui.imageLoader.LocalAppImageLoader
import su.afk.kemonos.ui.imageLoader.buildRemoteVideoPreviewImageRequest
import su.afk.kemonos.ui.presenter.androidView.clearHtml
import su.afk.kemonos.ui.uiUtils.format.buildVideoPreviewUrl

@Composable
internal fun PostPreview(
    preview: PreviewState,
    imgBaseUrl: String,
    uiSettingModel: UiSettingModel,
    title: String?,
    textPreview: String?,
    blurImage: Boolean,
) {
    val imageModifier = Modifier.fillMaxSize().then(if (blurImage) Modifier.blur(14.dp) else Modifier)

    when (preview) {
        is PreviewState.Image -> {
            AsyncImageWithStatus(
                model = "$imgBaseUrl/thumbnail/data${preview.path}",
                contentDescription = title,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }

        is PreviewState.Video -> {
            if (!uiSettingModel.showPreviewVideo) {
                PreviewPlaceholder(text = stringResource(R.string.video_section))
            } else if (uiSettingModel.useExternalMetaData) {
                RemoteVideoPostPreview(
                    videoPath = preview.path,
                    previewServerUrl = uiSettingModel.videoPreviewServerUrl,
                    title = title,
                    imageModifier = imageModifier,
                )
            } else {
                PreviewPlaceholder(text = stringResource(R.string.video_section))
            }
        }

        PreviewState.Audio -> {
            PreviewPlaceholder(text = stringResource(R.string.audio_file))
        }

        PreviewState.Empty -> {
            if (textPreview.isNullOrBlank()) {
                PreviewPlaceholder(text = "")
            } else {
                PreviewPlaceholder(textPreview.clearHtml())
            }
        }
    }
}

@Composable
private fun RemoteVideoPostPreview(
    videoPath: String?,
    previewServerUrl: String,
    title: String?,
    imageModifier: Modifier,
) {
    val context = LocalContext.current
    val videoPreviewUrl = remember(videoPath, previewServerUrl) {
        buildVideoPreviewUrl(
            videoPath = videoPath,
            enabled = true,
            previewServerUrl = previewServerUrl
        )
    }

    if (videoPreviewUrl == null) {
        PreviewPlaceholder(text = stringResource(R.string.video_section))
        return
    }

    val request = remember(videoPreviewUrl, context) {
        buildRemoteVideoPreviewImageRequest(
            context = context,
            url = videoPreviewUrl,
        )
    }

    val painter = rememberAsyncImagePainter(
        model = request,
        imageLoader = LocalAppImageLoader.current,
    )
    when (painter.state.collectAsStateWithLifecycle().value) {
        is AsyncImagePainter.State.Success -> {
            Image(
                painter = painter,
                contentDescription = title,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
            )
        }

        is AsyncImagePainter.State.Loading,
        is AsyncImagePainter.State.Error,
        is AsyncImagePainter.State.Empty -> {
            PreviewPlaceholder(text = stringResource(R.string.video_section))
        }
    }
}