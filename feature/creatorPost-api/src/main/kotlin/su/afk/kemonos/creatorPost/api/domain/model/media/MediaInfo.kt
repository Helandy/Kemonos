package su.afk.kemonos.creatorPost.api.domain.model.media

data class MediaInfo(
    val durationMs: Long,
    val sizeBytes: Long,
    val durationSeconds: Long? = null,
    val lastStatusCode: Int? = null,
)
