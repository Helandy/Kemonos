package su.afk.kemonos.creatorPost.presenter.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.webView.util.stripHtml
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.preferences.ui.DateFormatMode

internal fun LazyListScope.postCommentsSection(
    dateMode: DateFormatMode,
    commentDomains: List<CommentDomain>,
) {
    if (commentDomains.isEmpty()) return
    if (commentDomains.firstOrNull()?.id.isNullOrBlank()) return

    item(key = "comments_header") {
        Text(
            text = stringResource(R.string.comments_section),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }

    items(
        items = commentDomains,
        key = { c ->
            val idPart = c.id.ifBlank { "noid" }
            val pubPart = c.published ?: ""
            "comment:$idPart:$pubPart"
        }
    ) { comment ->
        CommentItem(
            dateMode = dateMode,
            comment = comment
        )

        Spacer(Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
internal fun CommentItem(
    dateMode: DateFormatMode,
    comment: CommentDomain,
) {
    val author = remember(comment.commenter_name, comment.commenter) {
        comment.commenter_name ?: comment.commenter
    }

    val content = remember(comment.content) {
        comment.content.stripHtml()
    }

    Column(modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
        Text(
            text = author,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 2.dp)
        )

        val published = comment.published.toUiDateTime(dateMode)
        if (published.isNotBlank()) {
            Text(
                text = "🕒 $published",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}