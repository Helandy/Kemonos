package su.afk.kemonos.creatorProfile.presenter.communityChat.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.URL_TAG
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.buildMediaUrls
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.buildMessageAnnotatedContent
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.toUiDateTimeWithTime
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.R

@Composable
internal fun MessageItem(
    message: CommunityMessage,
    fallbackBaseUrl: String,
    dateMode: DateFormatMode,
    onOpenMedia: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    translateExpandedIds: Set<String>,
    translateLoadingIds: Set<String>,
    translatedTextById: Map<String, String>,
    translateErrorById: Map<String, String>,
    onToggleTranslate: (String, String) -> Unit,
) {
    val creatorMessage = message.userRole.equals("campaign", ignoreCase = true)
    val bubbleColor = MaterialTheme.colorScheme.surfaceContainerLow
    val contentColor = MaterialTheme.colorScheme.onSurface
    val messageId = message.messageId
    val isTranslateExpanded = messageId in translateExpandedIds
    val isTranslateLoading = messageId in translateLoadingIds
    val translatedText = translatedTextById[messageId]
    val translateError = translateErrorById[messageId]
    val userNameColor = if (creatorMessage) {
        MaterialTheme.colorScheme.primary
    } else {
        contentColor
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = bubbleColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message.userName ?: message.userId,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = userNameColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (creatorMessage) {
                        Text(
                            text = "CREATOR",
                            color = Color(0xFFFFB94F),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = message.createdAt.toUiDateTimeWithTime(mode = dateMode),
                    color = contentColor.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            val isDeleted = message.deletedAt.isNullOrBlank().not()
            if (isDeleted) {
                Text(
                    text = "Message deleted",
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = contentColor.copy(alpha = 0.8f)
                )
            } else {
                val text = message.text.orEmpty()
                if (text.isNotBlank()) {
                    MessageText(
                        text = text,
                        textColor = contentColor,
                        modifier = Modifier.padding(top = 4.dp),
                        onOpenUrl = onOpenUrl
                    )
                }

                val mediaUrls = buildMediaUrls(message, fallbackBaseUrl)
                if (mediaUrls.isNotEmpty()) {
                    MediaGrid(
                        items = mediaUrls,
                        onOpenMedia = onOpenMedia,
                    )
                }

                if (text.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { onToggleTranslate(messageId, text) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    if (isTranslateExpanded) R.string.translate_hide else R.string.translate_show
                                ),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                if (isTranslateExpanded) {
                    Text(
                        text = when {
                            isTranslateLoading -> stringResource(R.string.translate_model_loading)
                            !translateError.isNullOrBlank() -> translateError
                            !translatedText.isNullOrBlank() -> translatedText
                            else -> stringResource(R.string.translate_hint)
                        },
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (!translateError.isNullOrBlank()) {
                            MaterialTheme.colorScheme.error
                        } else {
                            contentColor.copy(alpha = 0.88f)
                        }
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
                        .padding(top = 8.dp)
                        .clickable { expandedReplies = !expandedReplies }
                )

                if (expandedReplies) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        replies.forEach { reply ->
                            MessageItem(
                                message = reply,
                                fallbackBaseUrl = fallbackBaseUrl,
                                dateMode = dateMode,
                                onOpenMedia = onOpenMedia,
                                onOpenUrl = onOpenUrl,
                                translateExpandedIds = translateExpandedIds,
                                translateLoadingIds = translateLoadingIds,
                                translatedTextById = translatedTextById,
                                translateErrorById = translateErrorById,
                                onToggleTranslate = onToggleTranslate,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageText(
    text: String,
    textColor: Color,
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

    SelectionContainer(modifier = modifier) {
        @Suppress("DEPRECATION")
        ClickableText(
            text = annotatedText,
            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
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
}
