package su.afk.kemonos.common.presenter.screens.postsScreen.postCard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.screens.postsScreen.postCard.model.PreviewState
import su.afk.kemonos.common.presenter.screens.postsScreen.postCard.placeHolder.PreviewPlaceholder
import su.afk.kemonos.common.presenter.views.imageLoader.AsyncImageWithStatus

@Composable
internal fun PostPreview(
    preview: PreviewState,
    imgBaseUrl: String,
    title: String?,
    modifier: Modifier = Modifier
) {
    when (preview) {
        is PreviewState.Image -> {
            AsyncImageWithStatus(
                model = "$imgBaseUrl/thumbnail/data${preview.path}",
                contentDescription = title,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }

        PreviewState.Video -> PreviewPlaceholder(text = stringResource(R.string.video_file))
        PreviewState.Audio -> PreviewPlaceholder(text = stringResource(R.string.audio_file))
        PreviewState.Empty -> PreviewPlaceholder(text = "")
    }
}

@Composable
internal fun CornerBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(99.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 2.dp,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
        )
    }
}