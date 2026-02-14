package su.afk.kemonos.utils.url

fun buildBaseUrl(domain: String): String =
    "https://${domain.trim().trim('/')}/api/"