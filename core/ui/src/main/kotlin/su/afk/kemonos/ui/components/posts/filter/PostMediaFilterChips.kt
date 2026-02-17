package su.afk.kemonos.ui.components.posts.filter

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R

@Composable
fun PostMediaFilterChips(
    filter: PostMediaFilter,
    onToggleHasVideo: () -> Unit,
    onToggleHasAttachments: () -> Unit,
    onToggleHasImages: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = filter.hasVideo,
            onClick = onToggleHasVideo,
            label = { Text(stringResource(R.string.post_filter_contains_video)) },
        )
        FilterChip(
            selected = filter.hasAttachments,
            onClick = onToggleHasAttachments,
            label = { Text(stringResource(R.string.post_filter_contains_attachments)) },
        )
        FilterChip(
            selected = filter.hasImages,
            onClick = onToggleHasImages,
            label = { Text(stringResource(R.string.post_filter_contains_images)) },
        )
    }
}
