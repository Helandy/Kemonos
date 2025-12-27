package su.afk.kemonos.creatorProfile.api.domain.models.profileDms

import kotlinx.serialization.Serializable

@Serializable
data class Dm(
    val added: String,
    val content: String,
    val hash: String,
    val published: String,
    val service: String,
    val user: String,
)