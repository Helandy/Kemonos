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
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(url.toUri(), "video/*")
        title?.let { putExtra(Intent.EXTRA_TITLE, it) }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching {
        context.startActivity(intent)
    }.onFailure {
        context.toast(context.getString(R.string.video_open_failed))
    }
}
