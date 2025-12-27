package su.afk.kemonos.creatorProfile.api.domain.models.profileLinks

import kotlinx.serialization.Serializable

@Serializable
data class ProfileLink(
    val id: String,
    val indexed: String,
    val name: String,
    val publicId: String?,
    val relationId: Int?,
    val service: String,
    val updated: String?
)