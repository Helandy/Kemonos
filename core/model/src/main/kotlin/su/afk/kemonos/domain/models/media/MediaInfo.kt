package su.afk.kemonos.domain.models.media

data class MediaInfo(
    val durationMs: Long,
    val sizeBytes: Long,
    val durationSeconds: Long? = null,
    val lastStatusCode: Int? = null,
)
