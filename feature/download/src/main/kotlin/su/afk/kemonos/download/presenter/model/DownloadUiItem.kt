package su.afk.kemonos.download.presenter.model

import android.app.DownloadManager
import su.afk.kemonos.download.data.DownloadManagerSnapshot
import su.afk.kemonos.storage.api.repository.download.TrackedDownload

internal data class DownloadUiItem(
    val downloadId: Long,
    val title: String,
    val status: Int,
    val reasonCode: Int?,
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val speedBytesPerSec: Long,
    val mediaType: String?,
    val remoteUri: String?,
    val localUri: String?,
    val lastModifiedMs: Long?,
) {
    companion object {
        const val STATUS_REMOVED = -1

        fun from(
            tracked: TrackedDownload,
            snapshot: DownloadManagerSnapshot?,
            speedBytesPerSec: Long,
        ): DownloadUiItem {
            if (snapshot == null) {
                return DownloadUiItem(
                    downloadId = tracked.downloadId,
                    title = tracked.fileName ?: "download-${tracked.downloadId}",
                    status = STATUS_REMOVED,
                    reasonCode = null,
                    bytesDownloaded = 0L,
                    totalBytes = -1L,
                    speedBytesPerSec = 0L,
                    mediaType = null,
                    remoteUri = tracked.url,
                    localUri = null,
                    lastModifiedMs = null,
                )
            }

            val reasonCode = snapshot.reason.takeIf {
                snapshot.status == DownloadManager.STATUS_FAILED || snapshot.status == DownloadManager.STATUS_PAUSED
            }

            return DownloadUiItem(
                downloadId = tracked.downloadId,
                title = snapshot.title?.takeIf { it.isNotBlank() } ?: tracked.fileName
                ?: "download-${tracked.downloadId}",
                status = snapshot.status,
                reasonCode = reasonCode,
                bytesDownloaded = snapshot.bytesDownloaded,
                totalBytes = snapshot.totalBytes,
                speedBytesPerSec = speedBytesPerSec,
                mediaType = snapshot.mediaType,
                remoteUri = snapshot.remoteUri ?: tracked.url,
                localUri = snapshot.localUri,
                lastModifiedMs = snapshot.lastModifiedMs,
            )
        }

    }
}
