package su.afk.kemonos.storage.entity.favorites.artist

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist

@Entity(
    tableName = "favorite_artists",
    primaryKeys = ["site", "service", "id"],
    indices = [
        Index(value = ["site", "favedSeq"])
    ]
)
data class FavoriteArtistEntity(
    val site: SelectedSite,

    val id: String,
    val service: String,

    val favedSeq: Int,
    val indexed: String,
    val lastImported: String,
    val name: String,
    val publicId: String,
    val relationId: Int,
    val updated: String,
) {
    companion object {
        fun FavoriteArtist.toEntity(site: SelectedSite): FavoriteArtistEntity =
            FavoriteArtistEntity(
                site = site,
                id = id,
                service = service,
                favedSeq = favedSeq,
                indexed = indexed,
                lastImported = lastImported,
                name = name,
                publicId = publicId,
                relationId = relationId,
                updated = updated,
            )

        fun FavoriteArtistEntity.toDomain(): FavoriteArtist = FavoriteArtist(
            favedSeq = favedSeq,
            id = id,
            indexed = indexed,
            lastImported = lastImported,
            name = name,
            publicId = publicId,
            relationId = relationId,
            service = service,
            updated = updated,
        )
    }
}