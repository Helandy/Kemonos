package su.afk.kemonos.ui.video

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.toast.toast

/** Где открыть видео */
fun openVideoExternally(
    context: Context,
    url: String,
    title: String? = null,
) {
    val uri = url.toUri()
    val packageManager = context.packageManager

    val typedIntent = Intent(Intent.ACTION_VIEW).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        setDataAndType(uri, "video/*")
        title?.let { putExtra(Intent.EXTRA_TITLE, it) }
    }

    val urlIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        title?.let { putExtra(Intent.EXTRA_TITLE, it) }
    }

    val intent = when {
        typedIntent.resolveActivity(packageManager) != null -> typedIntent
        urlIntent.resolveActivity(packageManager) != null -> urlIntent
        else -> Intent.createChooser(urlIntent, title ?: "Open video").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    runCatching {
        context.startActivity(intent)
    }.onFailure {
        context.toast(context.getString(R.string.video_open_failed))
    }
}
