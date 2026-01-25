package su.afk.kemonos.deepLink.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri

private const val CHROME_PKG = "com.android.chrome"

// todo в будущем добавить настройку выбора браузера по умолчанию
/**
 * Открыть ссылку в Chrome, а если Chrome недоступен — показать chooser.
 */
fun openUrlPreferChrome(
    context: Context,
    rawUrl: String,
    chooserTitle: String = "Open in browser",
) {
    val trimmed = rawUrl.trim()
    if (trimmed.isBlank()) return

    val normalized = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
        trimmed
    } else {
        "https://$trimmed"
    }

    val uri: Uri = runCatching { normalized.toUri() }.getOrElse {
        Log.e("openUrlPreferChrome", "Bad url: $rawUrl", it)
        return
    }

    val chromeIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        setPackage(CHROME_PKG)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching {
        context.startActivity(chromeIntent)
    }.getOrElse { e ->
        Log.e("openUrlPreferChrome", "Chrome failed, fallback to chooser. url=$normalized", e)

        val base = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(base, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        runCatching { context.startActivity(chooser) }
            .onFailure { ee -> Log.e("openUrlPreferChrome", "Chooser failed. url=$normalized", ee) }
    }
}