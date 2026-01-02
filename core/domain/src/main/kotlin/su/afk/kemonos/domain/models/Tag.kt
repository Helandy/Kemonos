package su.afk.kemonos.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val tag: String,
    val postCount: Int
)