package su.afk.kemonos.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val id: Int,
    val username: String,
    val createdAt: String,
    val role: String,
)
