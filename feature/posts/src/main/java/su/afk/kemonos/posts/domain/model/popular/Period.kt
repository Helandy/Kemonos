package su.afk.kemonos.posts.domain.model.popular

import su.afk.kemonos.posts.data.dto.popular.request.PeriodDto

internal enum class Period {
    RECENT,
    DAY,
    WEEK,
    MONTH;

    companion object {
        fun Period.toDto() = when (this) {
            RECENT -> PeriodDto.RECENT
            DAY -> PeriodDto.DAY
            WEEK -> PeriodDto.WEEK
            MONTH -> PeriodDto.MONTH
        }
    }
}