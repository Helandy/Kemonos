package su.afk.kemonos.common.presenter.androidView

import java.net.URI

fun ensureTrailingSlash(url: String): String =
    if (url.endsWith("/")) url else "$url/"

fun resolveUrl(raw: String?, baseUrl: String): String {
    val s = raw?.trim().orEmpty()
    if (s.isBlank()) return ""

    val lower = s.lowercase()

    // оставляем как есть
    if (lower.startsWith("http://") || lower.startsWith("https://")) return s
    if (lower.startsWith("data:") || lower.startsWith("mailto:") || lower.startsWith("tel:")) return s
    if (lower.startsWith("kemonos://")) return s

    // protocol-relative
    if (s.startsWith("//")) return "https:$s"

    // часто встречается "data/...." без ведущего /
    val normalized = if (s.startsWith("data/")) "/$s" else s

    return runCatching {
        URI(ensureTrailingSlash(baseUrl)).resolve(normalized).toString()
    }.getOrDefault(s)
}