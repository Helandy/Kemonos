package su.afk.kemonos.common.util

import su.afk.kemonos.domain.models.AttachmentDomain
import java.net.URLEncoder

/**
 * Строит прямую ссылку на файл в стиле Kemono:
 * {server}/data{path}?f={encodedFileName}
 */
fun AttachmentDomain.buildDataUrl(fallbackBaseUrl: String? = null): String {
    val resolvedBaseUrl = server
        ?.trim()
        ?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: fallbackBaseUrl
            ?.trim()
            ?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: ""

    val base = "${resolvedBaseUrl.trimEnd('/')}/data${path}"
    val fileName = name?.takeIf { it.isNotBlank() } ?: path.substringAfterLast('/')
    val encoded = URLEncoder.encode(fileName, Charsets.UTF_8.name())
    return "$base?f=$encoded"
}
