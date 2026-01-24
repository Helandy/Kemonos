package su.afk.kemonos.common.presenter.androidView

private val MEDIA_TAG = Regex("""<(img|video|iframe|audio|source)\b""", RegexOption.IGNORE_CASE)
private val STRIP_TAGS = Regex("<[^>]*>")
private val NBSP = Regex("&nbsp;|&#160;", RegexOption.IGNORE_CASE)

fun isEffectivelyEmptyHtml(html: String): Boolean {
    /** если есть медиа */
    if (MEDIA_TAG.containsMatchIn(html)) return false

    val textOnly = html
        .replace(NBSP, " ")
        .replace(STRIP_TAGS, "")
        .trim()

    return textOnly.isBlank()
}