package su.afk.kemonos.common.presenter.webView.util

private val SRC_ROOT = Regex("""src\s*=\s*(['"])/""", RegexOption.IGNORE_CASE)
private val IMG_TAG = Regex("""<img\b[^>]*>""", RegexOption.IGNORE_CASE)

fun normalizeHtml(body: String, baseUrl: String): String {
    val base = baseUrl.trimEnd('/')

    // src="/..." -> src="https://kemono.cr/..."
    var out = body.replace(SRC_ROOT) { mr ->
        val quote = mr.groupValues[1]
        "src=$quote$base/"
    }

    // добавим loading="lazy" decoding="async" если нет
    out = out.replace(IMG_TAG) { mr ->
        val tag = mr.value
        if (tag.contains("loading=", ignoreCase = true)) tag
        else tag.dropLast(1) + """ loading="lazy" decoding="async">"""
    }

    return out
}