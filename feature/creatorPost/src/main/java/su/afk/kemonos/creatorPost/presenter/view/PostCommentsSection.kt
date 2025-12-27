package su.afk.kemonos.creatorPost.presenter.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain

@Composable
internal fun PostCommentsSection(commentDomains: List<CommentDomain>) {
    when {
        commentDomains.isEmpty() -> return
        commentDomains.first().id.isEmpty() -> return
        else -> {
            Text(
                stringResource(R.string.comments_section),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column {
                commentDomains.forEach { comment ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            comment.commenter_name ?: comment.commenter,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            comment.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "🕒 ${comment.published.toUiDateTime()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}