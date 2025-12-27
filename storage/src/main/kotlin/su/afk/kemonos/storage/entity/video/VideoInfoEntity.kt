package su.afk.kemonos.storage.entity.video

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import su.afk.kemonos.creatorPost.api.domain.model.VideoInfo

@Entity(
    tableName = "video_info",
    indices = [Index("createdAt")]
)
data class VideoInfoEntity(
    @PrimaryKey val name: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val createdAt: Long,
) {
    companion object {

        fun VideoInfoEntity.toDomain(): VideoInfo =
            VideoInfo(
                durationMs = durationMs,
                sizeBytes = sizeBytes
            )

        fun VideoInfo.toEntity(name: String): VideoInfoEntity =
            VideoInfoEntity(
                name = name,
                durationMs = durationMs,
                sizeBytes = sizeBytes,
                createdAt = System.currentTimeMillis()
            )
    }
}