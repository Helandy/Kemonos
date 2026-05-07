package su.afk.kemonos.domain.models.creator

import kotlinx.serialization.Serializable

@Serializable
enum class CreatorsSort {
    POPULARITY,
    INDEX,
    UPDATE,
    NAME,
}
