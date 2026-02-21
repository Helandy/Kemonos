package su.afk.kemonos.storage.entity.blacklist

import androidx.room.Entity
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor

@Entity(
    tableName = "blacklisted_authors",
    primaryKeys = ["service", "creatorId"]
)
internal data class BlacklistedAuthorEntity(
    val service: String,
    val creatorId: String,
    val creatorName: String,
    val createdAt: Long,
) {
    fun toDomain(): BlacklistedAuthor = BlacklistedAuthor(
        service = service,
        creatorId = creatorId,
        creatorName = creatorName,
        createdAt = createdAt,
    )

    companion object {
        fun fromDomain(author: BlacklistedAuthor): BlacklistedAuthorEntity = BlacklistedAuthorEntity(
            service = author.service,
            creatorId = author.creatorId,
            creatorName = author.creatorName,
            createdAt = author.createdAt,
        )
    }
}
