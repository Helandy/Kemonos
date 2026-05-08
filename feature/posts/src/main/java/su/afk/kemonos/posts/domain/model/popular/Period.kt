package su.afk.kemonos.posts.domain.model.popular

import kotlinx.serialization.Serializable

@Serializable
internal enum class Period {
    RECENT,
    DAY,
    WEEK,
    MONTH,
}
