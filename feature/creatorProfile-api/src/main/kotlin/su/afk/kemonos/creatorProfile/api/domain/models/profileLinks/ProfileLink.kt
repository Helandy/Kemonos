package su.afk.kemonos.creatorProfile.api.domain.models.profileLinks

import kotlinx.serialization.Serializable
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator

@Serializable
data class ProfileLink(
    val id: String,
    val indexed: String,
    val name: String,
    val publicId: String?,
    val relationId: Int?,
    val service: String,
    val updated: String?
) {
    companion object {
        fun SimilarCreator.toProfileLink() = ProfileLink(
            id = id,
            indexed = indexed,
            name = name,
            publicId = publicId,
            relationId = relationId,
            service = service,
            updated = updated
        )

    }
}