package su.afk.kemonos.posts.data.dto.popular.request

internal enum class PeriodDto(val apiValue: String) {
    RECENT("recent"),
    DAY("day"),
    WEEK("week"),
    MONTH("month");

    override fun toString(): String = apiValue
}