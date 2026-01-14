package su.afk.kemonos.creatorPost.presenter.view

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.afk.kemonos.common.R
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.presenter.webView.WebViewPool
import su.afk.kemonos.common.presenter.webView.rememberPooledWebView
import su.afk.kemonos.common.presenter.webView.util.isEffectivelyEmptyHtml
import su.afk.kemonos.common.presenter.webView.util.normalizeHtml
import su.afk.kemonos.common.presenter.webView.util.wrapHtml

@Composable
internal fun PostContentBlock(
    service: String,
    body: String,
    onOpenImage: (String) -> Unit,
) {
    if (body.isBlank()) return
    val isEffectivelyEmpty = remember(body) { isEffectivelyEmptyHtml(body) }
    if (isEffectivelyEmpty) return

    val context = LocalContext.current
    val resolver = LocalDomainResolver.current
    val siteBaseUrl = remember(service) { resolver.baseUrlByService(service) }

    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()
    val bgColor = MaterialTheme.colorScheme.background.toArgb()

    val htmlState by produceState<String?>(initialValue = null, body, siteBaseUrl, textColor, linkColor, bgColor) {
        value = withContext(Dispatchers.Default) {
            val normalized = normalizeHtml(
                body = body,
            )

            val withImgClickHook = normalized + IMAGE_CLICK_HOOK_JS

            wrapHtml(
                body = withImgClickHook,
                textColor = textColor,
                linkColor = linkColor,
                backgroundColor = bgColor,
                fontSizeSp = 16f
            )
        }
    }

    if (htmlState == null) {
        /** лёгкий плейсхолдер, чтобы экран не “висел” */
        Text(
            text = stringResource(R.string.loading),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val webView = rememberPooledWebView(
        context = context,
        bgColor = bgColor,
        onOpenUrl = { url ->
            val uri = url.toUri()

            /** Клик по картинке  */
            if (uri.scheme == "kemonos" && uri.host == "open_image") {
                val original = uri.getQueryParameter("url") ?: return@rememberPooledWebView
                onOpenImage(original)
                return@rememberPooledWebView
            }

            /** Прочие клики наружу */
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    ).apply {
        /** не блокировать долгий тап, иначе выделение/копирование не появится */
        /** false -> отдать обработку WebView (выделение текста, контекстное меню) */
        isLongClickable = true
        isHapticFeedbackEnabled = true
        setOnLongClickListener { false }
    }

    AndroidView(
        modifier = Modifier.padding(4.dp).fillMaxWidth().heightIn(min = 1.dp),
        factory = { webView },
        update = { view ->
            val html = htmlState!!
            val last = view.tag as? String
            if (last != html) {
                view.tag = html

                view.stopLoading()
                view.loadUrl("about:blank")
                view.post {
                    view.loadDataWithBaseURL(siteBaseUrl, html, "text/html", "utf-8", null)
                }
            }
        },
        onRelease = { view -> WebViewPool.release(view) }
    )
}

private const val IMAGE_CLICK_HOOK_JS = """
<script>
(function() {
  function absUrl(src) {
    try { return new URL(src, document.baseURI).href; } catch (e) { return src; }
  }

  document.addEventListener('click', function(e) {
    var el = e.target;
    if (!el) return;

    // Ловим клик по IMG
    if (el.tagName === 'IMG') {
      e.preventDefault();
      e.stopPropagation();

      var src = el.currentSrc || el.src || el.getAttribute('src');
      if (!src) return;

      src = absUrl(src);
      window.location.href = 'kemonos://open_image?url=' + encodeURIComponent(src);
    }
  }, true); // capture=true, чтобы перехватывать даже если IMG внутри <a>
})();
</script>
"""