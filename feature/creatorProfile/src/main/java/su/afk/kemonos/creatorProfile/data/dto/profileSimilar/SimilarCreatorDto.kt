package su.afk.kemonos.creatorProfile.data.dto.profileSimilar

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator

internal data class SimilarCreatorDto(
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
    val updated: String?,
    @SerializedName("score")
    val score: Double?
) {
    companion object {
        fun SimilarCreatorDto.toDomain(): SimilarCreator = SimilarCreator(
            id = id,
            indexed = indexed,
            name = name,
            publicId = publicId,
            relationId = relationId,
            service = service,
            updated = updated,
            score = score
        )

        fun List<SimilarCreatorDto>.toDomain(): List<SimilarCreator> = map { it.toDomain() }
    }
}
