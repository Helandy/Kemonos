package su.afk.kemonos.storage.entity.profile

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.domain.domain.models.Profile

@Entity(
    tableName = "profiles",
    primaryKeys = ["service", "id"],
    indices = [
        Index(value = ["cachedAt"]),
        Index(value = ["service", "id", "cachedAt"])
    ]
)
data class ProfileEntity(
    val id: String,
    val service: String,

    val name: String,
    val indexed: String?,
    val updated: String?,
    val publicId: String?,
    val hasChats: Boolean?,
    val relationId: Int?,

    val cachedAt: Long = System.currentTimeMillis(),

    val postCount: Int?,
    val dmCount: Int?,
    val shareCount: Int?,
    val chatCount: Int?,
) {
    companion object {
        fun Profile.toEntity(): ProfileEntity = ProfileEntity(
            id = id,
            service = service,
            name = name,
            indexed = indexed,
            updated = updated,
            publicId = publicId,
            hasChats = hasChats,
            relationId = relationId,
            cachedAt = System.currentTimeMillis(),
            postCount = postCount,
            dmCount = dmCount,
            shareCount = shareCount,
            chatCount = chatCount,
        )

        fun ProfileEntity.toDomain(): Profile = Profile(
            id = id,
            service = service,
            name = name,
            indexed = indexed.orEmpty(),
            updated = updated.orEmpty(),
            publicId = publicId,
            hasChats = hasChats,
            relationId = relationId,
            postCount = postCount,
            dmCount = dmCount,
            shareCount = shareCount,
            chatCount = chatCount,
        )
    }
}