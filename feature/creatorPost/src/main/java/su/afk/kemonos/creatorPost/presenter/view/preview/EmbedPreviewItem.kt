package su.afk.kemonos.creatorPost.presenter.view.preview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.domain.models.PreviewDomain

@Composable
fun EmbedPreviewItem(
    preview: PreviewDomain,
    onEmbedClick: (String) -> Unit,
) {
    preview.url?.let { url ->
        Text(
            text = "🔗 ${preview.subject.orEmpty()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onEmbedClick(url) }
                .padding(vertical = 8.dp)
        )
        Text(
            text = preview.description.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}