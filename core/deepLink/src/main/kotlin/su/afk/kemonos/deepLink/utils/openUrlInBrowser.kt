package su.afk.kemonos.deepLink.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

/**
 * Открыть ссылку в браузере по умолчанию, а если недоступно — показать chooser.
 */
fun openUrlInBrowser(
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
        return
    }

    val defaultBrowserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching {
        context.startActivity(defaultBrowserIntent)
    }.getOrElse {

        val base = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(base, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        runCatching { context.startActivity(chooser) }
            .onFailure {}
    }
}
