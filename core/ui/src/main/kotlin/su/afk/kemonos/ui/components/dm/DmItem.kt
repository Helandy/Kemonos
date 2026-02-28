package su.afk.kemonos.ui.components.dm

import android.util.Patterns
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.creator.CreatorListItem
import su.afk.kemonos.ui.date.toUiDateTime

private const val URL_TAG = "url"

@Composable
fun DmItem(
    dateMode: DateFormatMode,
    dm: DmUiItem,
    expanded: Boolean,
    onClick: () -> Unit,
    onCreatorClick: ((DmCreatorUi) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.Medium,
    )
    val annotatedContent = remember(dm.content, linkStyle) {
        buildDmAnnotatedContent(dm.content, linkStyle)
    }
    var collapsedHasOverflow by remember(dm.hash, dm.content) { mutableStateOf(true) }

    Column {
        dm.creator?.let { creator ->
            CreatorListItem(
                dateMode = dateMode,
                service = creator.service,
                id = creator.id,
                name = creator.name,
                updated = creator.updated,
                onClick = { onCreatorClick?.invoke(creator) }
            )
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .clickable { onClick() }
                    .animateContentSize()
                    .padding(12.dp)
            ) {
                if (expanded || !collapsedHasOverflow) {
                    SelectionContainer {
                        @Suppress("DEPRECATION")
                        ClickableText(
                            text = annotatedContent,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = if (expanded) Int.MAX_VALUE else 5,
                            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                            onClick = { offset ->
                                val url = annotatedContent
                                    .getStringAnnotations(URL_TAG, offset, offset)
                                    .firstOrNull()
                                    ?.item
                                    ?: return@ClickableText
                                uriHandler.openUri(url)
                            }
                        )
                    }
                } else {
                    Text(
                        text = annotatedContent,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            collapsedHasOverflow = result.hasVisualOverflow
                        }
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.dm_published, dm.published.toUiDateTime(dateMode)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun buildDmAnnotatedContent(content: String, linkStyle: SpanStyle): AnnotatedString {
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
            val normalizedUrl = normalizeUrl(rawUrl)
            pushStringAnnotation(tag = URL_TAG, annotation = normalizedUrl)
            pushStyle(linkStyle)
            append(rawUrl)
            pop()
            pop()
            currentIndex = end
        }
        if (currentIndex < content.length) {
            append(content.substring(currentIndex))
        }
    }
}

private fun normalizeUrl(url: String): String {
    val lower = url.lowercase()
    return if (lower.startsWith("http://") || lower.startsWith("https://")) url else "https://$url"
}
