package su.afk.kemonos.storage.entity.download

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracked_downloads",
    indices = [
        Index(value = ["createdAtMs"]),
    ]
)
internal data class DownloadTaskEntity(
    @PrimaryKey val downloadId: Long,
    val url: String,
    val fileName: String?,
    val service: String?,
    val creatorName: String?,
    val postId: String?,
    val postTitle: String?,
    val createdAtMs: Long,
    val lastStatus: Int?,
    val lastReason: Int?,
    val lastErrorLabel: String?,
    val lastSeenAtMs: Long?,
)
