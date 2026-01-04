package su.afk.kemonos.utils

fun buildBaseUrl(domain: String): String =
    "https://${domain.trim().trim('/')}/api/"