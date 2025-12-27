package su.afk.kemonos.domain.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val tag: String,
    val postCount: Int
)