package su.afk.kemonos.creatorProfile.presenter.communityChat.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.MessageItemActions
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.MessageItemUi
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.URL_TAG
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.buildMediaUrls
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.buildMessageAnnotatedContent
import su.afk.kemonos.creatorProfile.presenter.communityChat.utils.toUiDateTimeWithTime
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.ui.imageLoader.LocalAppImageLoader

@Composable
internal fun MessageItem(
    message: CommunityMessage,
    ui: MessageItemUi,
    actions: MessageItemActions,
) {
    val creatorMessage = message.userRole.equals("campaign", ignoreCase = true)
    val bubbleColor = MaterialTheme.colorScheme.surfaceContainerLow
    val contentColor = MaterialTheme.colorScheme.onSurface
    val messageId = message.messageId
    val isTranslateExpanded = ui.translation.isExpanded(messageId)
    val isTranslateLoading = ui.translation.isLoading(messageId)
    val translatedText = ui.translation.translatedText(messageId)
    val translateError = ui.translation.error(messageId)
    val userNameColor = if (creatorMessage) {
        MaterialTheme.colorScheme.primary
    } else {
        contentColor
    }
    val userLabel = message.userName ?: message.userId

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
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (ui.showAuthorAvatar) {
                        MessageAuthorAvatar(
                            avatarUrl = message.userAvatarUrl,
                            userLabel = userLabel
                        )
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = userLabel,
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
                }
                Text(
                    text = message.createdAt.toUiDateTimeWithTime(mode = ui.dateMode),
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
                        onOpenUrl = actions.onOpenUrl
                    )
                }

                val mediaUrls = buildMediaUrls(message, ui.fallbackBaseUrl)
                if (mediaUrls.isNotEmpty()) {
                    MediaGrid(
                        items = mediaUrls,
                        autoplayVideoInline = ui.autoplayVideoInline,
                        blurImage = ui.blurImages,
                        onOpenMedia = actions.onOpenMedia,
                    )
                }

                if (text.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { actions.onToggleTranslate(messageId, text) },
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
                                ui = ui,
                                actions = actions,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageAuthorAvatar(
    avatarUrl: String?,
    userLabel: String
) {
    val size = 30.dp
    val shape = RoundedCornerShape(8.dp)
    val fallbackPainter = painterResource(id = R.drawable.discordgrey)
    val imageLoader = LocalAppImageLoader.current
    val remoteUrl = avatarUrl?.trim().orEmpty().ifBlank { null }
    val avatarPainter = rememberAsyncImagePainter(
        model = remoteUrl,
        imageLoader = imageLoader
    )
    val painterState by avatarPainter.state.collectAsState()
    val showRemoteAvatar = remoteUrl != null && painterState is AsyncImagePainter.State.Success

    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = if (showRemoteAvatar) avatarPainter else fallbackPainter,
            contentDescription = "$userLabel avatar",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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
    val messageContent = remember(text, linkStyle) {
        buildMessageAnnotatedContentWithDiscordEmoji(text, linkStyle)
    }

    if (messageContent.inlineEmojiUrls.isEmpty()) {
        SelectionContainer(modifier = modifier) {
            @Suppress("DEPRECATION")
            ClickableText(
                text = messageContent.annotatedText,
                style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                onClick = { offset ->
                    val url = messageContent.annotatedText
                        .getStringAnnotations(URL_TAG, offset, offset)
                        .firstOrNull()
                        ?.item
                        ?: return@ClickableText
                    onOpenUrl(url)
                }
            )
        }
        return
    }

    val inlineContent = remember(messageContent.inlineEmojiUrls) {
        messageContent.inlineEmojiUrls.mapValues { (_, url) ->
            InlineTextContent(
                placeholder = Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                AsyncImageWithStatus(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
    var layoutResult by remember(messageContent.annotatedText) { mutableStateOf<TextLayoutResult?>(null) }

    SelectionContainer(modifier = modifier) {
        Text(
            text = messageContent.annotatedText,
            inlineContent = inlineContent,
            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            modifier = Modifier.pointerInput(messageContent.annotatedText) {
                detectTapGestures { position ->
                    val textLayout = layoutResult ?: return@detectTapGestures
                    val offset = textLayout.getOffsetForPosition(position)
                    val url = messageContent.annotatedText
                        .getStringAnnotations(URL_TAG, offset, offset)
                        .firstOrNull()
                        ?.item
                        ?: return@detectTapGestures
                    onOpenUrl(url)
                }
            },
            onTextLayout = { layoutResult = it }
        )
    }
}

private data class MessageAnnotatedContent(
    val annotatedText: AnnotatedString,
    val inlineEmojiUrls: Map<String, String>,
)

private val DISCORD_CUSTOM_EMOJI_REGEX = Regex("""<(a?):([A-Za-z0-9_]+):(\d+)>""")

private fun buildMessageAnnotatedContentWithDiscordEmoji(
    content: String,
    linkStyle: SpanStyle,
): MessageAnnotatedContent {
    val matches = DISCORD_CUSTOM_EMOJI_REGEX.findAll(content).toList()
    if (matches.isEmpty()) {
        return MessageAnnotatedContent(
            annotatedText = buildMessageAnnotatedContent(content, linkStyle),
            inlineEmojiUrls = emptyMap(),
        )
    }

    val inlineEmojiUrls = linkedMapOf<String, String>()
    val annotatedText = buildAnnotatedString {
        var currentIndex = 0
        var emojiIndex = 0

        matches.forEach { match ->
            val start = match.range.first
            val endExclusive = match.range.last + 1
            if (start > currentIndex) {
                append(buildMessageAnnotatedContent(content.substring(currentIndex, start), linkStyle))
            }

            val animated = match.groupValues[1] == "a"
            val emojiId = match.groupValues[3]
            val inlineKey = "discord_emoji_$emojiIndex"
            emojiIndex += 1

            inlineEmojiUrls[inlineKey] = if (animated) {
                "https://cdn.discordapp.com/emojis/$emojiId.gif"
            } else {
                "https://cdn.discordapp.com/emojis/$emojiId.png"
            }
            appendInlineContent(id = inlineKey, alternateText = match.groupValues[2])

            currentIndex = endExclusive
        }

        if (currentIndex < content.length) {
            append(buildMessageAnnotatedContent(content.substring(currentIndex), linkStyle))
        }
    }

    return MessageAnnotatedContent(
        annotatedText = annotatedText,
        inlineEmojiUrls = inlineEmojiUrls,
    )
}
