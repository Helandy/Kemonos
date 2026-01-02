package su.afk.kemonos.storage.entity.tags

import androidx.room.Entity
import su.afk.kemonos.posts.api.tags.Tags

@Entity(
    tableName = "tags",
    primaryKeys = ["tags"]
)
data class TagsEntity(
    val tags: String,
    val count: Int?,
) {
    companion object {
        fun Tags.toEntity(): TagsEntity = TagsEntity(
            tags = tags ?: "",
            count = count,
        )

        fun TagsEntity.toDomain(): Tags = Tags(
            tags = tags,
            count = count,
        )
    }
}