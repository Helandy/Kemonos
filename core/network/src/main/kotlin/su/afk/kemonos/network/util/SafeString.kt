package su.afk.kemonos.network.util

import okhttp3.ResponseBody

fun ResponseBody.safeString(
    maxChars: Int = 20_000
): String? =
    runCatching {
        val s = string()
        if (s.length > maxChars) s.take(maxChars) + "\n…(truncated)" else s
    }.getOrNull()


fun String.extractBackendError(): String? {
    val trimmed = trim()
    if (trimmed.isEmpty()) return null

    /** JSON: {"error":"Invalid username"} */
    val regex = """"error"\s*:\s*"([^"]+)"""".toRegex()
    return regex.find(trimmed)?.groupValues?.getOrNull(1)
}