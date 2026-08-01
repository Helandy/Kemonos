package su.afk.kemonos.storage.entity.localLikes

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist

@Entity(
    tableName = "local_liked_artists",
    primaryKeys = ["site", "service", "id"],
    indices = [
        Index(value = ["site", "likedAt"]),
    ]
)
data class LocalLikedArtistEntity(
    val site: SelectedSite,

    val id: String,
    val service: String,

    val name: String,
    val indexed: String,
    val publicId: String,
    val relationId: Int,
    val updated: String,

    val likedAt: Long,
) {
    companion object {
        fun FavoriteArtist.toEntity(site: SelectedSite, likedAt: Long = System.currentTimeMillis()): LocalLikedArtistEntity =
            LocalLikedArtistEntity(
                site = site,
                id = id,
                service = service,
                name = name,
                indexed = indexed,
                publicId = publicId,
                relationId = relationId,
                updated = updated,
                likedAt = likedAt,
            )

        fun LocalLikedArtistEntity.toDomain(): FavoriteArtist = FavoriteArtist(
            favedSeq = 0,
            id = id,
            indexed = indexed,
            lastImported = "",
            name = name,
            publicId = publicId,
            relationId = relationId,
            service = service,
            updated = updated,
        )
    }
}
