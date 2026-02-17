package su.afk.kemonos.ui.shared

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ShareActions {
    fun copyToClipboard(
        context: Context,
        label: String,
        text: String,
    ) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
    }
}
