package su.afk.kemonos.storage.entity.localLikes

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.domain.SelectedSite

@Entity(
    tableName = "local_liked_posts",
    primaryKeys = ["site", "service", "userId", "id"],
    indices = [
        Index(value = ["site", "userId"]),
        Index(value = ["site", "likedAt"]),
    ]
)
data class LocalLikedPostEntity(
    val site: SelectedSite,

    val id: String,
    val userId: String,
    val service: String,

    val title: String?,
    val content: String?,
    val substring: String?,
    val added: String?,
    val published: String?,
    val edited: String?,

    val incompleteRewardsJson: String?,
    val pollJson: String?,
    val fileName: String?,
    val filePath: String?,
    val attachmentsJson: String?,
    val tagsJson: String?,

    val nextId: String?,
    val prevId: String?,

    val likedAt: Long,
)
