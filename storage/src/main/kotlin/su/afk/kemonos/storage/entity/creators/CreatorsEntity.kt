package su.afk.kemonos.storage.entity.creators

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import su.afk.kemonos.domain.domain.models.Creators

@Entity(
    tableName = "creators",
    indices = [
        Index("service"),
        Index("favorited"),
        Index("indexed"),
        Index("updated"),
        Index(value = ["service", "favorited"]),
        Index(value = ["service", "indexed"]),
        Index(value = ["service", "updated"]),
        Index(value = ["service", "name"]),
        Index("name"),
    ]
)
data class CreatorsEntity(
    @PrimaryKey val id: String,
    val name: String,
    val service: String,
    val favorited: Int,
    val indexed: Int,
    val updated: Int
) {
    companion object {
        fun Creators.toEntity() = CreatorsEntity(
            id = id,
            name = name,
            service = service,
            favorited = favorited,
            indexed = indexed,
            updated = updated
        )

        fun CreatorsEntity.toDomain() = Creators(
            id = id,
            name = name,
            service = service,
            favorited = favorited,
            indexed = indexed,
            updated = updated
        )
    }
}