package su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards

import kotlinx.serialization.Serializable

@Serializable
data class ProfileFanCard(
    val added: String,
    val ctime: String,
    val ext: String,
    val fileId: Int,
    val hash: String,
    val id: Int,
    val lastCheckedAt: String,
    val mime: String,
    val mtime: String,
    val path: String,
    val price: String,
    val server: String,
    val size: Int,
    val userId: String
)