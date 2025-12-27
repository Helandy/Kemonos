package su.afk.kemonos.creatorProfile.data.dto.profileLinks


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink

internal data class ProfileLinksDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("indexed")
    val indexed: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("public_id")
    val publicId: String?,
    @SerializedName("relation_id")
    val relationId: Int?,
    @SerializedName("service")
    val service: String,
    @SerializedName("updated")
    val updated: String
) {
    companion object {
        fun ProfileLinksDto.toDomain(): ProfileLink = ProfileLink(
            id = id,
            indexed = indexed,
            name = name,
            publicId = publicId,
            relationId = relationId,
            service = service,
            updated = updated
        )

        fun List<ProfileLinksDto>.toDomain(): List<ProfileLink> = map { it.toDomain() }
    }
}