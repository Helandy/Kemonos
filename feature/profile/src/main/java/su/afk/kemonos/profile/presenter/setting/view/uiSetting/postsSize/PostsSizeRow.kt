package su.afk.kemonos.profile.presenter.setting.view.uiSetting.postsSize

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.PostsSize
import su.afk.kemonos.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostsSizeRow(
    title: String,
    value: PostsSize,
    onChange: (PostsSize) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = value == PostsSize.SMALL,
                onClick = { onChange(PostsSize.SMALL) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                label = { Text(stringResource(R.string.settings_posts_size_small)) },
            )
            SegmentedButton(
                selected = value == PostsSize.MEDIUM,
                onClick = { onChange(PostsSize.MEDIUM) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                label = { Text(stringResource(R.string.settings_posts_size_medium)) },
            )
            SegmentedButton(
                selected = value == PostsSize.LARGE,
                onClick = { onChange(PostsSize.LARGE) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                label = { Text(stringResource(R.string.settings_posts_size_large)) },
            )
        }
    }
    Divider()
}