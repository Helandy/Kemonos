package su.afk.kemonos.download.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.preferences.ui.DownloadFolderMode
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import javax.inject.Inject

private const val APP_DOWNLOAD_DIR = "Kemonos"
private const val MAX_CREATOR_LEN = 50
private const val MAX_SERVICE_LEN = 40
private const val MAX_TITLE_DIR_LEN = 32

/**
 * Скачивает файл через системный DownloadManager.
 */
internal class DownloadUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uiSetting: IUiSettingUseCase,
) : IDownloadUtil {

    override fun enqueueSystemDownload(
        url: String,
        fileName: String?,
        service: String?,
        creatorName: String?,
        postId: String?,
        postTitle: String?,
    ): Long {
        val settings = runBlocking { uiSetting.prefs.first() }

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(url.toUri())
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        request.setTitle(fileName?.takeIf { it.isNotBlank() } ?: url)

        val safeName = (fileName ?: guessFileNameFromUrl(url))
            .sanitizeFileName()
            .ifBlank { "download" }

        val subDir = buildSubDir(
            mode = settings.downloadFolderMode,
            addServiceName = settings.addServiceName,
            service = service,
            creatorName = creatorName,
            postId = postId,
            postTitle = postTitle,
        )

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "$subDir/$safeName"
        )

        return dm.enqueue(request)
    }
}

private fun buildSubDir(
    mode: DownloadFolderMode,
    addServiceName: Boolean,
    service: String?,
    creatorName: String?,
    postId: String?,
    postTitle: String?,
): String {
    val creator = creatorName.orEmpty().sanitizePathPart(MAX_CREATOR_LEN).ifBlank { "creator" }
    val pid = postId.orEmpty().sanitizePathPart(32).ifBlank { "post" }

    val titleRaw = postTitle.orEmpty()
    val title = titleRaw.sanitizePathPart(MAX_TITLE_DIR_LEN).ifBlank { "post" }

    // маленький стабильный суффикс, чтобы не было коллизий при обрезке
    val titleSuffix = titleRaw.stableSuffix(6)
    val titleWithSuffix = if (titleSuffix.isNotBlank()) "${title}_$titleSuffix" else title

    val core = when (mode) {
        DownloadFolderMode.CREATOR ->
            creator

        DownloadFolderMode.CREATOR_POST_ID ->
            "$creator/$pid"

        DownloadFolderMode.CREATOR_POST_TITLE_ID ->
            "$creator/${titleWithSuffix}_$pid"

        DownloadFolderMode.POST_ID ->
            pid

        DownloadFolderMode.POST_TITLE_ID ->
            "${titleWithSuffix}_$pid"
    }

    val prefix = if (addServiceName) {
        val s = service.orEmpty().sanitizePathPart(MAX_SERVICE_LEN).ifBlank { "service" }
        "$APP_DOWNLOAD_DIR/$s"
    } else {
        APP_DOWNLOAD_DIR
    }

    return "$prefix/$core"
}

private fun String.stableSuffix(len: Int): String {
    val s = trim()
    if (s.isBlank()) return ""
    val h = s.hashCode().toUInt().toString(16)
    return h.take(len)
}

private fun String.sanitizePathPart(maxLen: Int): String =
    replace(Regex("""[\\/:*?"<>|]"""), "_")
        .replace(Regex("""[.\u2026]+"""), "")
        .replace(Regex("""\s+"""), " ")
        .trim()
        .take(maxLen)


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
        .take(120)