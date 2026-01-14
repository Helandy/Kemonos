package su.afk.kemonos.common.presenter.webView

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

object WebViewPool {
    private val pool = ArrayDeque<WebView>(1)
    private var prewarmed = false

    /** прогрев при запуске апки */
    fun prewarm(
        context: Context,
        bgColor: Int,
        onOpenUrl: (String) -> Unit = {}
    ) {
        if (prewarmed) return

        val mainLooper = android.os.Looper.getMainLooper()
        if (android.os.Looper.myLooper() != mainLooper) {
            android.os.Handler(mainLooper).post {
                prewarm(context, bgColor, onOpenUrl)
            }
            return
        }

        prewarmed = true
        if (pool.isNotEmpty()) return

        val wv = acquire(context, bgColor, onOpenUrl)
        runCatching { wv.loadUrl("about:blank") }

        (wv.parent as? ViewGroup)?.removeView(wv)
        pool.addLast(wv)
    }

    fun acquire(
        context: Context,
        bgColor: Int,
        onOpenUrl: (String) -> Unit
    ): WebView {
        val appCtx = context.applicationContext
        val wv = pool.removeFirstOrNull() ?: WebView(appCtx)

        /** переиспользуем, надо отцепить от прошлого родителя */
        (wv.parent as? ViewGroup)?.removeView(wv)

        wv.setBackgroundColor(bgColor)

        // todo Подумать о варианте выкидывания webView
        wv.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = false
            loadsImagesAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            displayZoomControls = false
            mediaPlaybackRequiresUserGesture = true

            /** кэш пусть работает */
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        }

        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url?.toString().orEmpty()
                if (url.isBlank()) return false
                onOpenUrl(url)
                return true
            }
        }

        return wv
    }

    fun release(wv: WebView) {
        runCatching {
            (wv.parent as? ViewGroup)?.removeView(wv)
            wv.stopLoading()
            wv.loadUrl("about:blank")
            wv.clearHistory()
            wv.tag = null
        }
        if (pool.isEmpty()) pool.addLast(wv) else wv.destroy()
    }
}

@Composable
fun rememberPooledWebView(
    context: Context,
    bgColor: Int,
    onOpenUrl: (String) -> Unit
): WebView {
    val latestOnOpenUrl = rememberUpdatedState(onOpenUrl)

    return remember(bgColor) {
        WebViewPool.acquire(context, bgColor) { url ->
            latestOnOpenUrl.value(url)
        }
    }
}