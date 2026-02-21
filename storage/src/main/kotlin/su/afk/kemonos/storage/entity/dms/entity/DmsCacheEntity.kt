package su.afk.kemonos.storage.entity.dms.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "dms_cache",
    primaryKeys = ["queryKey", "offset", "hash"],
    indices = [
        Index(value = ["queryKey", "offset"]),
        Index(value = ["updatedAt"]),
    ]
)
data class DmsCacheEntity(
    val queryKey: String,
    val offset: Int,
    val hash: String,

    val service: String,
    val user: String,
    val content: String,
    val added: String,
    val published: String,
    val artistId: String,
    val artistName: String,
    val artistUpdated: String?,

    val indexInPage: Int,
    val updatedAt: Long,
)
