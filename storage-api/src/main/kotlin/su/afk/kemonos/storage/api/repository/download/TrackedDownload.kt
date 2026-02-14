package su.afk.kemonos.storage.api.repository.download

data class TrackedDownload(
    val downloadId: Long,
    val url: String,
    val fileName: String?,
    val service: String?,
    val creatorName: String?,
    val postId: String?,
    val postTitle: String?,
    val createdAtMs: Long,
)
