package su.afk.kemonos.common.video

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

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

    context.startActivity(intent)
}