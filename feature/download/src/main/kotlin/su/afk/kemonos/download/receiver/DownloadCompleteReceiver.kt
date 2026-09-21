package su.afk.kemonos.download.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.download.notification.DownloadProgressNotifier
import su.afk.kemonos.download.webp.DownloadWebpConverter
import su.afk.kemonos.download.webp.WebpConversionResult
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface DownloadCompleteEntryPoint {
    fun downloadManagerDataSource(): DownloadManagerDataSource
    fun trackedDownloadsRepository(): ITrackedDownloadsRepository
    fun webpConverter(): DownloadWebpConverter
    fun progressNotifier(): DownloadProgressNotifier
    fun uiSetting(): IUiSettingUseCase
}

/**
 * Досжатие скачанного файла в webp.
 *
 * DownloadManager пишет файл сам, поэтому пережать его можно только после того,
 * как он сообщил о завершении.
 *
 * Зависимости берём через EntryPoint, а не @AndroidEntryPoint: инъекция в
 * приёмник требует вызвать super.onReceive, а он у BroadcastReceiver абстрактный.
 */
internal class DownloadCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return

        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        if (downloadId <= 0L) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DownloadCompleteEntryPoint::class.java,
        )

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val settings = entryPoint.uiSetting().prefs.first()
                if (settings.downloadSingleNotification) {
                    entryPoint.progressNotifier().refresh()
                }
                entryPoint.convertIfEnabled(downloadId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun DownloadCompleteEntryPoint.convertIfEnabled(downloadId: Long) {
        val settings = uiSetting().prefs.first()
        if (!settings.downloadConvertToWebp) return

        /** Чужие загрузки нас не касаются. */
        val tracked = trackedDownloadsRepository().observeAll().first()
            .firstOrNull { it.downloadId == downloadId } ?: return

        val snapshot = downloadManagerDataSource()
            .querySnapshots(listOf(downloadId))[downloadId] ?: return
        if (snapshot.status != DownloadManager.STATUS_SUCCESSFUL) return

        val localUri = snapshot.localUri?.takeIf { it.isNotBlank() } ?: return
        val fileName = snapshot.title?.takeIf { it.isNotBlank() }
            ?: tracked.fileName?.takeIf { it.isNotBlank() }
            ?: return

        val result = webpConverter().convert(
            localUri = localUri,
            fileName = fileName,
            quality = settings.downloadWebpQuality,
        )
        if (result is WebpConversionResult.Converted) {
            /** Список загрузок должен показывать то имя, что реально лежит на диске. */
            trackedDownloadsRepository().upsert(tracked.copy(fileName = result.fileName))
        }
    }
}
