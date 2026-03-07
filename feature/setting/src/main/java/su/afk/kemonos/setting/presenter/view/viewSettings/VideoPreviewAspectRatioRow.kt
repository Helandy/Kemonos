package su.afk.kemonos.setting.presenter.view.viewSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.VideoPreviewAspectRatio
import su.afk.kemonos.setting.R

@Composable
internal fun VideoPreviewAspectRatioRow(
    value: VideoPreviewAspectRatio,
    onChange: (VideoPreviewAspectRatio) -> Unit,
) {
    val options = listOf(
        VideoPreviewAspectRatio.RATIO_16_9 to R.string.settings_video_preview_ratio_16_9,
        VideoPreviewAspectRatio.RATIO_2_1 to R.string.settings_video_preview_ratio_2_1,
        VideoPreviewAspectRatio.RATIO_1_1 to R.string.settings_video_preview_ratio_1_1,
        VideoPreviewAspectRatio.RATIO_1_2 to R.string.settings_video_preview_ratio_1_2,
        VideoPreviewAspectRatio.RATIO_3F_2F to R.string.settings_video_preview_ratio_3f_2f,
        VideoPreviewAspectRatio.RATIO_2F_3F to R.string.settings_video_preview_ratio_2f_3f,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_video_preview_ratio_title),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.settings_video_preview_ratio_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
        ) {
            items(options) { (ratio, labelRes) ->
                FilterChip(
                    selected = value == ratio,
                    onClick = { onChange(ratio) },
                    label = { Text(stringResource(labelRes)) },
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .aspectRatio(value.ratio),
            shape = RoundedCornerShape(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                )
            }
        }
    }

    HorizontalDivider()
}
