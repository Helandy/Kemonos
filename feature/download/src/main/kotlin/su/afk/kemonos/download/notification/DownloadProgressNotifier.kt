package su.afk.kemonos.download.notification

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import su.afk.kemonos.download.R
import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Одно уведомление на все загрузки: «готово N из M» с полосой прогресса.
 *
 * Пересобирается по событию — при постановке файла в очередь и при завершении
 * каждого, — поэтому опрашивать DownloadManager по таймеру не нужно.
 */
@Singleton
internal class DownloadProgressNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
    private val downloadManagerDataSource: DownloadManagerDataSource,
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    /**
     * Идентификаторы текущего захода.
     *
     * DownloadManager помнит и давно завершённые загрузки, поэтому считать все
     * подряд нельзя: одна новая показала бы «100 из 101». В счёт идёт только то,
     * что мы застали в работе, — и то, что завершилось уже при нас.
     */
    private val runIds = linkedSetOf<Long>()

    /** Если уведомления запрещены, прятать штатные было бы обманом: молчим. */
    fun canNotify(): Boolean = notificationManager.areNotificationsEnabled()

    suspend fun refresh() {
        if (!canNotify()) return

        val ids = trackedDownloadsRepository.observeAll().first().map { it.downloadId }
        if (ids.isEmpty()) {
            cancel()
            return
        }

        val snapshots = downloadManagerDataSource.querySnapshots(ids)

        synchronized(runIds) {
            snapshots.forEach { (id, snapshot) ->
                if (snapshot.status.isActive()) runIds += id
            }
            /** О тех, кого DownloadManager уже забыл, докладывать нечего. */
            runIds.retainAll(snapshots.keys)
        }

        val runStatuses = synchronized(runIds) { runIds.toList() }
            .mapNotNull { id -> snapshots[id]?.status }

        val progress = DownloadProgress.from(runStatuses)

        if (!progress.hasAnything || progress.isFinished) {
            cancel()
            return
        }

        notify(progress)
    }

    fun cancel() {
        synchronized(runIds) { runIds.clear() }
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun Int.isActive(): Boolean =
        this == DownloadManager.STATUS_PENDING ||
                this == DownloadManager.STATUS_RUNNING ||
                this == DownloadManager.STATUS_PAUSED

    private fun notify(progress: DownloadProgress) {
        ensureChannel()

        val done = progress.completed + progress.failed
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(context.getString(R.string.downloads_notification_title))
            .setContentText(
                context.getString(R.string.downloads_notification_progress, done, progress.total)
            )
            .setProgress(progress.total, done, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        runCatching { notificationManager.notify(NOTIFICATION_ID, notification) }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.downloads_notification_channel),
            NotificationManager.IMPORTANCE_LOW,
        )
        notificationManager.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "kemonos_downloads_progress"
        const val NOTIFICATION_ID = 4711
    }
}
