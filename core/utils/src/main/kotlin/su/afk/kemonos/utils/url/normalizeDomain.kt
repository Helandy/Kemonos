package su.afk.kemonos.utils.url

fun normalizeDomain(baseUrl: String): String =
    baseUrl.trim()
        .removePrefix("https://")
        .removePrefix("http://")
        .removeSuffix("/api/")
        .removeSuffix("/api")
        .trim('/')