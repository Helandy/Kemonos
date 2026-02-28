package su.afk.kemonos.creatorProfile.presenter.communityChat.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.URL_TAG
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.buildMediaUrls
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.buildMessageAnnotatedContent
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.toUiDateTimeWithTime
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
internal fun MessageItem(
    message: CommunityMessage,
    fallbackBaseUrl: String,
    dateMode: DateFormatMode,
    onOpenMedia: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    depth: Int = 0
) {
    val leftPadding = (depth * 14).dp
    val creatorMessage = message.userRole.equals("campaign", ignoreCase = true)
    val bubbleColor = if (creatorMessage) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = leftPadding, end = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bubbleColor)
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message.userName ?: message.userId,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (message.userRole.equals("campaign", ignoreCase = true)) {
                Text(
                    text = "CREATOR",
                    color = Color(0xFFFFB94F),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = message.createdAt.toUiDateTimeWithTime(mode = dateMode),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall
            )
        }

        val isDeleted = message.deletedAt.isNullOrBlank().not()
        if (isDeleted) {
            Text(
                text = "Message deleted",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val text = message.text.orEmpty()
            if (text.isNotBlank()) {
                MessageText(
                    text = text,
                    modifier = Modifier.padding(top = 4.dp),
                    onOpenUrl = onOpenUrl
                )
            }
        }

        if (!isDeleted) {
            val mediaUrls = buildMediaUrls(message, fallbackBaseUrl)
            if (mediaUrls.isNotEmpty()) {
                MediaGrid(
                    items = mediaUrls,
                    onOpenMedia = onOpenMedia
                )
            }
        }

        val replies = message.replies.take(3)
        if (replies.isNotEmpty()) {
            var expandedReplies by rememberSaveable(message.messageId) { mutableStateOf(false) }
            Text(
                text = if (expandedReplies) "v ${replies.size} Replies" else "> ${replies.size} Replies",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { expandedReplies = !expandedReplies }
            )

            if (expandedReplies) {
                replies.forEach { reply ->
                    MessageItem(
                        message = reply,
                        fallbackBaseUrl = fallbackBaseUrl,
                        dateMode = dateMode,
                        onOpenMedia = onOpenMedia,
                        onOpenUrl = onOpenUrl,
                        depth = depth + 1
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageText(
    text: String,
    modifier: Modifier = Modifier,
    onOpenUrl: (String) -> Unit,
) {
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.Medium,
    )
    val annotatedText = remember(text, linkStyle) {
        buildMessageAnnotatedContent(text, linkStyle)
    }

    @Suppress("DEPRECATION")
    ClickableText(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
        onClick = { offset ->
            val url = annotatedText
                .getStringAnnotations(URL_TAG, offset, offset)
                .firstOrNull()
                ?.item
                ?: return@ClickableText
            onOpenUrl(url)
        }
    )
}