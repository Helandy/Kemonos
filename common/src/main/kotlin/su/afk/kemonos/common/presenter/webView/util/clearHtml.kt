package su.afk.kemonos.common.presenter.webView.util

import androidx.core.text.HtmlCompat

fun String.clearHtml(): String {
    if (isBlank()) return this
    val spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
    return spanned.toString()
        .replace('\u00A0', ' ')
        .replace(Regex("[ \\t\\x0B\\f\\r]+"), " ")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
}