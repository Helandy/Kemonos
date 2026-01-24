package su.afk.kemonos.common.presenter.androidView

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

private class CallbackUrlSpan(
    url: String,
    private val onClickUrl: (String) -> Unit
) : URLSpan(url) {
    override fun onClick(widget: android.view.View) = onClickUrl(url)
}

@Composable
fun HtmlTextBlock(
    html: String,
    modifier: Modifier = Modifier,
    onOpenUrl: (String) -> Unit,
) {
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()
    val fontSizeSp = MaterialTheme.typography.bodyLarge.fontSize.value

    val spanned = remember(html) {
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                setTextIsSelectable(true)
                movementMethod = LinkMovementMethod.getInstance()
                setTextColor(textColor)
                setLinkTextColor(linkColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSp)
                // чтобы не было “серого” фона при тапе
                highlightColor = android.graphics.Color.TRANSPARENT
            }
        },
        update = { tv ->
            tv.setTextColor(textColor)
            tv.setLinkTextColor(linkColor)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSp)

            val sp = spanned
            val ss = SpannableString(sp)

            // заменяем URLSpan на наш (чтобы не уходить “по умолчанию”)
            val spans = ss.getSpans(0, ss.length, URLSpan::class.java)
            spans.forEach { span ->
                val start = ss.getSpanStart(span)
                val end = ss.getSpanEnd(span)
                val flags = ss.getSpanFlags(span)
                ss.removeSpan(span)
                ss.setSpan(CallbackUrlSpan(span.url, onOpenUrl), start, end, flags)
            }

            tv.text = ss as Spanned
        }
    )
}