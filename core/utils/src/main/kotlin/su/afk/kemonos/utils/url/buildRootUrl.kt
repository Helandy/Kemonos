package su.afk.kemonos.utils.url

fun buildRootUrl(domain: String): String {
    val normalized = normalizeDomain(domain)
    return if (normalized.isBlank()) "" else "https://$normalized"
}
