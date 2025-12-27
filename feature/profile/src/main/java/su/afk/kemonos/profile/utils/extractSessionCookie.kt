package su.afk.kemonos.profile.utils

import okhttp3.Headers

fun extractSessionCookie(headers: Headers): String? {
    /** все Set-Cookie */
    val cookies = headers.values("Set-Cookie")
    return cookies
        .firstOrNull { it.startsWith("session=") }
        ?.substringAfter("session=")
        ?.substringBefore(";")
}