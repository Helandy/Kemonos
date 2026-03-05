package su.afk.kemonos.posts.data.dto.popular.request

import su.afk.kemonos.posts.domain.model.popular.Period

internal fun Period.toDto(): PeriodDto = when (this) {
    Period.RECENT -> PeriodDto.RECENT
    Period.DAY -> PeriodDto.DAY
    Period.WEEK -> PeriodDto.WEEK
    Period.MONTH -> PeriodDto.MONTH
}
