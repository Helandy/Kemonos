package su.afk.kemonos.creatorProfile.presenter.communityChat

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatState.*
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatState.State
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.header.CreatorCenterBackTopBar
import su.afk.kemonos.deepLink.utils.openUrlInBrowser
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.uiUtils.format.isImageFile
import su.afk.kemonos.ui.uiUtils.format.isVideoFile
import su.afk.kemonos.utils.url.buildContentUrlToDataSite
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommunityChatScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>
) {
    val context = LocalContext.current
    val resolver = LocalDomainResolver.current
    val fallbackBaseUrl = resolver.baseUrlByService(state.service)
    val listState = rememberLazyListState()

    LaunchedEffect(effect) {
        effect.collect {
            when (it) {
                is Effect.OpenUrl -> openUrlInBrowser(context, it.url)
            }
        }
    }
    LaunchedEffect(listState, state.messages.size, state.canLoadMore, state.loadingMore) {
        if (state.messages.isEmpty()) return@LaunchedEffect

        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .filter { lastVisibleIndex ->
                val thresholdIndex = (state.messages.lastIndex - 3).coerceAtLeast(0)
                state.canLoadMore && !state.loadingMore && lastVisibleIndex >= thresholdIndex
            }
            .collect { onEvent(Event.LoadMore) }
    }

    BaseScreen(
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CreatorCenterBackTopBar(
                title = state.channelName,
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior
            )
        },
        isLoading = state.loading,
        onRetry = { onEvent(Event.Retry) },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.messages.size) { index ->
                MessageItem(
                    message = state.messages[index],
                    fallbackBaseUrl = fallbackBaseUrl,
                    dateMode = state.uiSettingModel.dateFormatMode,
                    onOpenMedia = { url -> onEvent(Event.OpenMedia(url)) },
                    onOpenUrl = { url -> onEvent(Event.OpenUrl(url)) },
                )
            }

            item {
                if (state.loadingMore) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageItem(
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

private const val URL_TAG = "url"

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

private fun buildMessageAnnotatedContent(content: String, linkStyle: SpanStyle): AnnotatedString {
    val matcher = Patterns.WEB_URL.matcher(content)
    return buildAnnotatedString {
        var currentIndex = 0
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            if (start > currentIndex) {
                append(content.substring(currentIndex, start))
            }

            val rawUrl = content.substring(start, end)
            if (isLikelyUrl(rawUrl)) {
                val normalizedUrl = normalizeUrl(rawUrl)
                pushStringAnnotation(tag = URL_TAG, annotation = normalizedUrl)
                pushStyle(linkStyle)
                append(rawUrl)
                pop()
                pop()
            } else {
                append(rawUrl)
            }
            currentIndex = end
        }

        if (currentIndex < content.length) {
            append(content.substring(currentIndex))
        }
    }
}

private fun isLikelyUrl(rawUrl: String): Boolean {
    val candidate = rawUrl.trim()
    val lower = candidate.lowercase()
    if (lower.startsWith("http://") || lower.startsWith("https://")) return true
    if (lower.startsWith("www.")) return true

    val host = candidate.substringBefore('/')
    if (!host.contains('.')) return false
    if (!host.all { it.isLetterOrDigit() || it == '.' || it == '-' }) return false

    val tld = host.substringAfterLast('.', missingDelimiterValue = "")
    if (tld.length !in 2..4) return false
    if (!tld.all(Char::isLetter)) return false

    return true
}

private fun normalizeUrl(url: String): String {
    val lower = url.lowercase()
    return if (lower.startsWith("http://") || lower.startsWith("https://")) url else "https://$url"
}

private data class CommunityMedia(
    val previewUrl: String,
    val openUrl: String,
    val pathOrUrl: String,
)

@Composable
private fun MediaGrid(
    items: List<CommunityMedia>,
    onOpenMedia: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowItems.forEach { media ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (isImageFile(media.pathOrUrl)) {
                            Log.e("super", "media.previewUrl ${media.previewUrl}")
                            AsyncImageWithStatus(
                                model = media.previewUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenMedia(media.openUrl) },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onOpenMedia(media.openUrl) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isVideoFile(media.pathOrUrl)) "Video" else "Open file",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun buildMediaUrls(message: CommunityMessage, fallbackBaseUrl: String): List<CommunityMedia> {
    val fromAttachments = message.attachments.mapNotNull { att ->
        val path = att.path?.takeIf { it.isNotBlank() }
        if (path != null) {
            val fullUrl = AttachmentDomain(
                server = null,
                path = path,
                name = att.name
            ).buildContentUrlToDataSite(fallbackBaseUrl = fallbackBaseUrl)
            val previewUrl = buildThumbnailUrl(path = path, fallbackBaseUrl = fallbackBaseUrl)
            return@mapNotNull CommunityMedia(
                previewUrl = previewUrl,
                openUrl = fullUrl,
                pathOrUrl = path
            )
        }

        val thumb = att.thumbUrl?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        CommunityMedia(
            previewUrl = thumb,
            openUrl = thumb,
            pathOrUrl = thumb
        )
    }

    val fromEmbeds = message.embeds.mapNotNull { embed ->
        val image = embed.imageUrl?.takeIf { it.isNotBlank() }
            ?: embed.thumbUrl?.takeIf { it.isNotBlank() }
            ?: return@mapNotNull null
        if (!isImageFile(image) && !isVideoFile(image)) return@mapNotNull null
        CommunityMedia(
            previewUrl = image,
            openUrl = image,
            pathOrUrl = image
        )
    }

    return fromAttachments + fromEmbeds
}

private fun buildThumbnailUrl(path: String, fallbackBaseUrl: String): String {
    val base = fallbackBaseUrl.trim().trimEnd('/')
    return if (base.isBlank()) "/thumbnail/data$path" else "$base/thumbnail/data$path"
}

private fun String.toUiDateTimeWithTime(mode: DateFormatMode): String {
    val dateTime = toLocalDateTimeOrNull(zoneId = ZoneId.systemDefault())
    if (dateTime == null) {
        return toUiDateTime(mode = mode)
    }

    val date = dateTime.toUiDateTime(mode)
    val time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
    return "$date $time"
}

private fun String.toLocalDateTimeOrNull(zoneId: ZoneId): LocalDateTime? {
    val source = trim()
    if (source.isBlank()) return null

    if (source.endsWith("Z", ignoreCase = true) || source.contains('+') || source.lastIndexOf('-') > 9) {
        runCatching {
            return Instant.parse(source).atZone(zoneId).toLocalDateTime()
        }
        runCatching {
            return OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .atZoneSameInstant(zoneId)
                .toLocalDateTime()
        }
    }

    runCatching {
        return LocalDateTime.parse(source)
    }
    runCatching {
        return LocalDateTime.parse(source.substringBefore('Z'))
    }
    return null
}
