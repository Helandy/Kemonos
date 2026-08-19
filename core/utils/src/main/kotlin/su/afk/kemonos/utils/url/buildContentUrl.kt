package su.afk.kemonos.utils.url

import su.afk.kemonos.domain.MediaUrlScheme
import su.afk.kemonos.domain.models.AttachmentDomain
import java.net.URLEncoder

/**
 * Прямая ссылка на файл вложения.
 *
 * kemono-подобные источники: {server}/data{path}?f={имя}
 * OnlyHaven: {server}{path}?f={имя}, где path уже полный.
 */
fun AttachmentDomain.buildContentUrl(
    scheme: MediaUrlScheme,
    fallbackBaseUrl: String? = null,
): String {
    val resolvedBaseUrl = server
        ?.trim()
        ?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: fallbackBaseUrl
            ?.trim()
            ?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: ""

    val prefix = when (scheme) {
        MediaUrlScheme.DATA_PREFIXED -> "/data"
        MediaUrlScheme.DIRECT -> ""
    }
    val base = "${resolvedBaseUrl.trimEnd('/')}$prefix$path"
    val fileName = name?.takeIf { it.isNotBlank() } ?: path.substringAfterLast('/')
    val encoded = URLEncoder.encode(fileName, Charsets.UTF_8.name())
    return "$base?f=$encoded"
}
