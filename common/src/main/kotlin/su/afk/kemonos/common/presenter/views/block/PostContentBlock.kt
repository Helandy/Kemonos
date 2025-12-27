package su.afk.kemonos.common.presenter.views.block

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import kotlinx.coroutines.withContext
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.webView.WebViewPool
import su.afk.kemonos.common.presenter.webView.rememberPooledWebView
import su.afk.kemonos.common.presenter.webView.util.normalizeHtml
import su.afk.kemonos.common.presenter.webView.util.wrapHtml
import su.afk.kemonos.common.util.selectDomain.getBaseUrlByService

@Composable
fun PostContentBlock(
    service: String,
    body: String
) {
    if (body.isBlank()) return

    val context = LocalContext.current
    val imgBaseUrl = remember(service) { getBaseUrlByService(service) }

    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()
    val bgColor = MaterialTheme.colorScheme.background.toArgb()

    val htmlState by produceState<String?>(initialValue = null, body, imgBaseUrl, textColor, linkColor, bgColor) {
        value = withContext(kotlinx.coroutines.Dispatchers.Default) {
            val normalized = normalizeHtml(body, imgBaseUrl)
            wrapHtml(
                body = normalized,
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
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    )

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { webView },
        update = { view ->
            val html = htmlState!!
            val last = view.tag as? String
            if (last != html) {
                view.tag = html
                view.loadDataWithBaseURL(imgBaseUrl, html, "text/html", "utf-8", null)
            }
        },
        onRelease = { view -> WebViewPool.release(view) }
    )
}