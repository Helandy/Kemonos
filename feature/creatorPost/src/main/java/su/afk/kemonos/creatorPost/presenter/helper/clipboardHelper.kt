package su.afk.kemonos.creatorPost.presenter.helper

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

suspend fun copyTextToClipboard(
    clipboard: Clipboard,
    label: String,
    text: String,
) {
    val clip = ClipData.newPlainText(label, text)
    clipboard.setClipEntry(ClipEntry(clip))
}
