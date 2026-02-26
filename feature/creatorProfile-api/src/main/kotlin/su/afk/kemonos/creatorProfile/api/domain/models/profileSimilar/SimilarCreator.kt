package su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar

import kotlinx.serialization.Serializable

@Serializable
data class SimilarCreator(
    val id: String,
    val indexed: String,
    val name: String,
    val publicId: String?,
    val relationId: Int?,
    val service: String,
    val updated: String?,
    val score: Double?
)
