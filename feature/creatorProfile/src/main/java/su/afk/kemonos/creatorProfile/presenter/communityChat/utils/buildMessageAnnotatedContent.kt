package su.afk.kemonos.creatorProfile.presenter.communityChat.utils

import android.util.Patterns
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.CommunityMedia
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.uiUtils.format.isImageFile
import su.afk.kemonos.ui.uiUtils.format.isVideoFile
import su.afk.kemonos.utils.url.buildContentUrlToDataSite
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal const val URL_TAG = "url"

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

internal fun buildMessageAnnotatedContent(content: String, linkStyle: SpanStyle): AnnotatedString {
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

internal fun buildMediaUrls(message: CommunityMessage, fallbackBaseUrl: String): List<CommunityMedia> {
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

internal fun buildThumbnailUrl(path: String, fallbackBaseUrl: String): String {
    val base = fallbackBaseUrl.trim().trimEnd('/')
    return if (base.isBlank()) "/thumbnail/data$path" else "$base/thumbnail/data$path"
}

internal fun String.toUiDateTimeWithTime(mode: DateFormatMode): String {
    val dateTime = toLocalDateTimeOrNull(zoneId = ZoneId.systemDefault())
    if (dateTime == null) {
        return toUiDateTime(mode = mode)
    }

    val date = dateTime.toUiDateTime(mode)
    val time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
    return "$date $time"
}

internal fun String.toLocalDateTimeOrNull(zoneId: ZoneId): LocalDateTime? {
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
