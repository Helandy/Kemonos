package su.afk.kemonos.download.domain.model

internal data class DownloadManagerSnapshot(
    val title: String?,
    val status: Int,
    val reason: Int,
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val mediaType: String?,
    val remoteUri: String?,
    val localUri: String?,
    val lastModifiedMs: Long?,
)
