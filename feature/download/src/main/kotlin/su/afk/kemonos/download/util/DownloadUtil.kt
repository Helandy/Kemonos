package su.afk.kemonos.download.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.kemonos.download.api.IDownloadUtil
import javax.inject.Inject

/**
 * Скачивает файл через системный DownloadManager.
 */
internal class DownloadUtil @Inject constructor(
    @ApplicationContext private val context: Context,
) : IDownloadUtil {

    override fun enqueueSystemDownload(
        url: String,
        fileName: String?,
        mimeType: String?,
    ): Long {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(url.toUri())
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

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
}

private fun guessFileNameFromUrl(url: String): String {
    val u = url.toUri()

    val fromQuery = u.getQueryParameter("f")
    if (!fromQuery.isNullOrBlank()) return fromQuery

    return u.lastPathSegment ?: "download"
}

/** Windows-несовместимые и bad символы */
private fun String.sanitizeFileName(): String =
    replace(Regex("""[\\/:*?"<>|]"""), "_")
        .replace("\n", " ")
        .replace("\r", " ")
        .trim()