package su.afk.kemonos.creatorPost.domain.videoInfo.model

import su.afk.kemonos.domain.models.media.MediaInfo

internal data class VideoInfo(
    val path: String,

    val sizeBytes: Long,

    val durationSeconds: Long,

    val lastStatusCode: Int,
) {
    companion object {
        fun VideoInfo.toStorageMediaInfo(): MediaInfo =
            MediaInfo(
                durationMs = durationSeconds * 1000L,
                sizeBytes = sizeBytes,
                durationSeconds = durationSeconds,
                lastStatusCode = lastStatusCode,
            )
    }
}
