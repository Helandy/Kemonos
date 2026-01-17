package su.afk.kemonos.creatorPost.presenter.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import java.util.*

fun openGoogleTranslate(context: Context, text: String, targetLangTag: String) {
    val cleaned = text.trim()
    if (cleaned.isBlank()) return

    val tl = (if (targetLangTag.isBlank()) Locale.getDefault().language else targetLangTag).trim()

    // 1) Пробуем deep link с указанием языка
    val deepLink = Uri.parse(
        "googletranslate://translate?sl=auto&tl=${Uri.encode(tl)}&text=${Uri.encode(cleaned)}"
    )
    val deepIntent = Intent(Intent.ACTION_VIEW, deepLink).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching {
        context.startActivity(deepIntent)
        return
    }

    // 2) Фолбэк: ACTION_SEND в приложение (обычно текст не теряется)
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, cleaned)
        setPackage("com.google.android.apps.translate")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching {
        context.startActivity(sendIntent)
    }.getOrElse {
        // 3) Фолбэк на веб
        val url = "https://translate.google.com/?sl=auto&tl=${Uri.encode(tl)}&text=${Uri.encode(cleaned)}&op=translate"
        context.startActivity(
            Intent(Intent.ACTION_VIEW, url.toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}