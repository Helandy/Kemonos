package su.afk.kemonos.creatorPost.presenter.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R

@Composable
internal fun TagsRow(
    tags: List<String>?,
    showHeader: Boolean = true,
    onTagClick: (String) -> Unit = {},
) {
    if (tags.isNullOrEmpty()) return

    if (showHeader) {
        Text(
            text = stringResource(R.string.tags),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }

    FlowRow(
        modifier = Modifier.padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tags.forEach { tag ->
            Surface(
                onClick = { onTagClick(tag) },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 1.dp,
            ) {
                Text(
                    text = "#$tag",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
