package su.afk.kemonos.common.util

import su.afk.kemonos.domain.models.AttachmentDomain
import java.net.URLEncoder

/**
 * Строит прямую ссылку на файл в стиле Kemono:
 * {server}/data{path}?f={encodedFileName}
 */
fun AttachmentDomain.buildDataUrl(): String {
    val base = "${server}/data${path}"
    val fileName = name?.takeIf { it.isNotBlank() } ?: path.substringAfterLast('/')
    val encoded = URLEncoder.encode(fileName, Charsets.UTF_8.name())
    return "$base?f=$encoded"
}