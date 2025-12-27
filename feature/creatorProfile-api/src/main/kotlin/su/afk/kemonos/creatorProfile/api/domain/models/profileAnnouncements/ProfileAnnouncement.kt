package su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements

import kotlinx.serialization.Serializable

@Serializable
data class ProfileAnnouncement(
    val added: String,
    val content: String,
    val hash: String,
    val service: String,
    val userId: String
)