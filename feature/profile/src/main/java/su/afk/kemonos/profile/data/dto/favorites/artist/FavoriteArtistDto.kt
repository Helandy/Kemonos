package su.afk.kemonos.profile.data.dto.favorites.artist

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.creator.FavoriteArtist

internal data class FavoriteArtistDto(
    @SerializedName("faved_seq")
    val favedSeq: Int,

    @SerializedName("id")
    val id: String,

    @SerializedName("indexed")
    val indexed: String?,

    @SerializedName("last_imported")
    val lastImported: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("public_id")
    val publicId: String?,

    @SerializedName("relation_id")
    val relationId: Int,

    @SerializedName("service")
    val service: String,

    @SerializedName("updated")
    val updated: String?
) {
    companion object {
        fun FavoriteArtistDto.toDomain() = FavoriteArtist(
            favedSeq = favedSeq,
            id = id,
            indexed = indexed.orEmpty(),
            lastImported = lastImported.orEmpty(),
            name = name,
            publicId = publicId.orEmpty(),
            relationId = relationId,
            service = service,
            updated = updated.orEmpty()
        )
    }
}