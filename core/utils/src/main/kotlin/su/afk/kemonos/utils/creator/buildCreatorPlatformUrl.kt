package su.afk.kemonos.utils.creator

fun buildCreatorPlatformUrl(service: String, publicId: String?, id: String): String? {
    val slug = publicId?.trim()?.removePrefix("@")?.ifBlank { null } ?: id.trim().ifBlank { return null }

    return when (service.lowercase()) {
        "patreon" -> "https://www.patreon.com/$slug"
        "fanbox" -> "https://www.fanbox.cc/@$slug"
        "onlyfans" -> "https://onlyfans.com/$slug"
        "fansly" -> "https://fansly.com/$slug"
        "candfans" -> "https://candfans.jp/$slug"
        "boosty" -> "https://boosty.to/$slug"
        "fantia" -> "https://fantia.jp/fanclubs/$slug"
        "gumroad" -> "https://$slug.gumroad.com"
        "subscribestar", "subscriblestar" -> "https://subscribestar.adult/$slug"
        "dlsite", "dlslite" -> "https://www.dlsite.com/home/circle/profile/=/maker_id/$slug.html?locale=en_US"
        else -> null
    }
}
