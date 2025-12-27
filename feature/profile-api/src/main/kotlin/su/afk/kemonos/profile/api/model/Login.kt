package su.afk.kemonos.profile.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val id: Int,
    val username: String,
    val createdAt: String,
    val role: String,
)