package su.afk.kemonos.download.presenter.model

import android.app.DownloadManager
import su.afk.kemonos.download.data.DownloadManagerSnapshot
import su.afk.kemonos.storage.api.repository.download.TrackedDownload

internal data class DownloadUiItem(
    val downloadId: Long,
    val title: String,
    val status: Int,
    val statusLabel: String,
    val reasonCode: Int?,
    val reasonLabel: String?,
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
                    statusLabel = "Removed from DownloadManager",
                    reasonCode = null,
                    reasonLabel = null,
                    bytesDownloaded = 0L,
                    totalBytes = -1L,
                    speedBytesPerSec = 0L,
                    mediaType = null,
                    remoteUri = tracked.url,
                    localUri = null,
                    lastModifiedMs = null,
                )
            }

            val reasonLabel = statusReasonLabel(snapshot.status, snapshot.reason)
            val reasonCode = snapshot.reason.takeIf {
                snapshot.status == DownloadManager.STATUS_FAILED || snapshot.status == DownloadManager.STATUS_PAUSED
            }

            return DownloadUiItem(
                downloadId = tracked.downloadId,
                title = snapshot.title?.takeIf { it.isNotBlank() } ?: tracked.fileName
                ?: "download-${tracked.downloadId}",
                status = snapshot.status,
                statusLabel = statusLabel(snapshot.status),
                reasonCode = reasonCode,
                reasonLabel = reasonLabel,
                bytesDownloaded = snapshot.bytesDownloaded,
                totalBytes = snapshot.totalBytes,
                speedBytesPerSec = speedBytesPerSec,
                mediaType = snapshot.mediaType,
                remoteUri = snapshot.remoteUri ?: tracked.url,
                localUri = snapshot.localUri,
                lastModifiedMs = snapshot.lastModifiedMs,
            )
        }

        private fun statusLabel(status: Int): String = when (status) {
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Running"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_SUCCESSFUL -> "Completed"
            DownloadManager.STATUS_FAILED -> "Failed"
            else -> "Unknown"
        }

        private fun statusReasonLabel(status: Int, reason: Int): String? = when (status) {
            DownloadManager.STATUS_FAILED -> when (reason) {
                DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume"
                DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Device not found"
                DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists"
                DownloadManager.ERROR_FILE_ERROR -> "File error"
                DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP data error"
                DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient space"
                DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
                DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Unhandled HTTP code"
                DownloadManager.ERROR_UNKNOWN -> "Unknown error"
                else -> "Error $reason"
            }

            DownloadManager.STATUS_PAUSED -> when (reason) {
                DownloadManager.PAUSED_QUEUED_FOR_WIFI -> "Queued for Wi-Fi"
                DownloadManager.PAUSED_WAITING_FOR_NETWORK -> "Waiting for network"
                DownloadManager.PAUSED_WAITING_TO_RETRY -> "Waiting to retry"
                DownloadManager.PAUSED_UNKNOWN -> "Paused"
                else -> "Paused $reason"
            }

            else -> null
        }
    }
}
