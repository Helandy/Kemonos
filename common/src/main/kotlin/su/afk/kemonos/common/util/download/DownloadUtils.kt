package su.afk.kemonos.common.util.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri

/**
 * Скачивает файл через системный DownloadManager.
 * Ничего “в приложении” писать не нужно — всё делает система.
 */
fun enqueueSystemDownload(
    context: Context,
    url: String,
    fileName: String? = null,
    mimeType: String? = null,
): Long {
    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val request = DownloadManager.Request(url.toUri())
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    /** Заголовок в шторке */
    request.setTitle(fileName?.takeIf { it.isNotBlank() } ?: url)

    /** MIME */
    mimeType?.takeIf { it.isNotBlank() }?.let(request::setMimeType)

    /** Куда класть (публичная папка Downloads) */
    val safeName = (fileName ?: guessFileNameFromUrl(url))
        .sanitizeFileName()
        .ifBlank { "download" }

    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, safeName)

    return dm.enqueue(request)
}

private fun guessFileNameFromUrl(url: String): String {
    val u = Uri.parse(url)

    /** пробуем f=... */
    val fromQuery = u.getQueryParameter("f")
    if (!fromQuery.isNullOrBlank()) return fromQuery

    /** иначе — последний сегмент пути */
    val last = u.lastPathSegment
    return last ?: "download"
}

private fun String.sanitizeFileName(): String {
    /** Windows-несовместимые и bad символы */
    return replace(Regex("""[\\/:*?"<>|]"""), "_")
        .replace("\n", " ")
        .replace("\r", " ")
        .trim()
}