package su.afk.kemonos.posts.data.dto.popular.response

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.data.dto.PostUnifiedDto
import su.afk.kemonos.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.posts.api.popular.PopularInfo
import su.afk.kemonos.posts.api.popular.PopularNavigationDates
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.posts.api.popular.PopularProps
import su.afk.kemonos.posts.data.dto.popular.response.PopularInfoDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.popular.response.PopularNavigationDatesDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.popular.response.PopularPropsDto.Companion.toDomain

internal data class PopularPostsDto(
    @SerializedName("props")
    val props: PopularPropsDto,
    @SerializedName("info")
    val info: PopularInfoDto,
    @SerializedName("posts")
    val posts: List<PostUnifiedDto>,
) {
    companion object {
        fun PopularPostsDto.toDomain() = PopularPosts(
            props = this.props.toDomain(),
            info = this.info.toDomain(),
            posts = this.posts.map { it.toDomain() }
        )
    }
}

internal data class PopularPropsDto(
    @SerializedName("count")
    val count: Int,
    @SerializedName("earliest_date_for_popular")
    val earliestDateForPopular: String?,
    @SerializedName("today")
    val today: String
) {
    companion object {
        fun PopularPropsDto.toDomain() = PopularProps(
            count = this.count,
            earliestDateForPopular = this.earliestDateForPopular,
            today = this.today,
        )
    }
}

internal data class PopularInfoDto(
    @SerializedName("date")
    val date: String?,
    @SerializedName("max_date")
    val maxDate: String?,
    @SerializedName("min_date")
    val minDate: String?,
    @SerializedName("navigation_dates")
    val navigationDates: PopularNavigationDatesDto?,
    @SerializedName("range_desc")
    val rangeDesc: String?,
    @SerializedName("scale")
    val scale: String?
) {
    companion object {
        fun PopularInfoDto.toDomain() = PopularInfo(
            date = this.date,
            maxDate = this.maxDate,
            minDate = this.minDate,
            navigationDates = this.navigationDates?.toDomain(),
            rangeDesc = this.rangeDesc,
            scale = this.scale,
        )
    }
}

internal data class PopularNavigationDatesDto(
    @SerializedName("recent")
    val recent: List<String>?,
    @SerializedName("day")
    val day: List<String>?,
    @SerializedName("month")
    val month: List<String>?,
    @SerializedName("week")
    val week: List<String>?
) {
    companion object {
        fun PopularNavigationDatesDto.toDomain() = PopularNavigationDates(
            recent = this.recent.orEmpty(),
            day = this.day.orEmpty(),
            month = this.month.orEmpty(),
            week = this.week.orEmpty(),
        )
    }
}

