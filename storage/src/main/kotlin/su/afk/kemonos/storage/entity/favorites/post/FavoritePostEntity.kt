package su.afk.kemonos.storage.entity.favorites.post

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.domain.SelectedSite

@Entity(
    tableName = "favorite_posts",
    primaryKeys = ["site", "service", "userId", "id"],
    indices = [
        Index(value = ["site", "userId"]),
        Index(value = ["site", "cachedAt"]),
    ]
)
data class FavoritePostEntity(
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

    val fileName: String?,
    val filePath: String?,
    val attachmentsJson: String?,
    val tagsJson: String?,

    val nextId: String?,
    val prevId: String?,
    val favedSeq: Int?,
    val favCount: Int?,

    val cachedAt: Long,
)