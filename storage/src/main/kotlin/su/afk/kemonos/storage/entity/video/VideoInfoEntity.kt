package su.afk.kemonos.storage.entity.video

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.domain.SelectedSite

@Entity(
    tableName = "video_info",
    primaryKeys = ["site", "path"],
    indices = [Index("createdAt")]
)
data class VideoInfoEntity(
    val site: SelectedSite,
    val path: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val durationSeconds: Long?,
    val lastStatusCode: Int?,
    val createdAt: Long,
) {
    companion object {

        fun VideoInfoEntity.toDomain(): MediaInfo =
            MediaInfo(
                durationMs = durationMs,
                sizeBytes = sizeBytes,
                durationSeconds = durationSeconds,
                lastStatusCode = lastStatusCode,
            )

        fun MediaInfo.toEntity(site: SelectedSite, path: String): VideoInfoEntity =
            VideoInfoEntity(
                site = site,
                path = path,
                durationMs = durationMs,
                sizeBytes = sizeBytes,
                durationSeconds = durationSeconds,
                lastStatusCode = lastStatusCode,
                createdAt = System.currentTimeMillis()
            )
    }
}
