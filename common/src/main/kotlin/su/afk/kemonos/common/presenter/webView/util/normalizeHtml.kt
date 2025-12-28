package su.afk.kemonos.common.presenter.webView.util

private val ATTR_URL = Regex(
    """\b(src|href|data-src|poster)\s*=\s*(['"])([^'"]*)\2""",
    RegexOption.IGNORE_CASE
)

private val IMG_TAG = Regex("""<img\b[^>]*>""", RegexOption.IGNORE_CASE)

fun normalizeHtml(
    body: String,
): String {
    var out = body.replace(ATTR_URL) { mr ->
        val attr = mr.groupValues[1]
        val quote = mr.groupValues[2]
        val url = mr.groupValues[3].trim()

        val fixed = when {
            url.isBlank() -> url

            /**
             * /img.kemono.cr/...
             * */
            url.startsWith("//") ->
                "https:$url"

            url.startsWith("http://", true) ||
                    url.startsWith("https://", true) ->
                url

            /** относительный от корня — WebView разрулит через baseUrl */
            url.startsWith("/") ->
                url

            else -> url
        }

        """$attr=$quote$fixed$quote"""
    }

    out = out.replace(IMG_TAG) { mr ->
        val tag = mr.value
        if (tag.contains("loading=", true)) tag
        else tag.dropLast(1) + """ loading="lazy" decoding="async">"""
    }

    return out
}