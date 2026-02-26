package su.afk.kemonos.creatorProfile.presenter.creatorProfile.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.models.Tag

@Composable
fun TagsScreen(
    tags: List<Tag>,
    onTagClick: (Tag) -> Unit = {},
) {
    FlowRow(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            key(tag.hashCode()) {
                TagChip(tag, onClick = { onTagClick(tag) })
            }
        }
    }
}

@Composable
fun TagChip(
    tag: Tag,
    onClick: () -> Unit
) {
    Text(
        text = "#${tag.tag} (${tag.postCount})",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}