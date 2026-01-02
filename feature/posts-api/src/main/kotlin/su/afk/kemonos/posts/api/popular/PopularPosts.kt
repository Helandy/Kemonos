package su.afk.kemonos.posts.api.popular

import kotlinx.serialization.Serializable
import su.afk.kemonos.domain.models.PostDomain

@Serializable
data class PopularPosts(
    val props: PopularProps,
    val info: PopularInfo,
    val posts: List<PostDomain>,
)

@Serializable
data class PopularProps(
    val count: Int,
    val earliestDateForPopular: String?,
    val today: String
)

@Serializable
data class PopularInfo(
    val date: String?,
    val maxDate: String?,
    val minDate: String?,
    val navigationDates: PopularNavigationDates?,
    val rangeDesc: String?,
    val scale: String?
)

@Serializable
data class PopularNavigationDates(
    val recent: List<String>,
    val day: List<String>,
    val month: List<String>,
    val week: List<String>
)