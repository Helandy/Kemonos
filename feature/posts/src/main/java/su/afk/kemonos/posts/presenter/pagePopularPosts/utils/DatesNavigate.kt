package su.afk.kemonos.posts.presenter.pagePopularPosts.utils

import android.content.Context
import su.afk.kemonos.posts.R
import su.afk.kemonos.posts.api.popular.PopularNavigationDates
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.preferences.ui.DateFormatMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Берём из navigation_dates нужный список и превращаем в Triple(prev, current, next)
 * формат у тебя в JSON: [prev, next, current]
 */
internal fun PopularNavigationDates?.tripleFor(
    period: Period
): Triple<String?, String?, String?>? {
    if (this == null) return null

    val list = when (period) {
        Period.DAY -> day
        Period.WEEK -> week
        Period.MONTH -> month
        Period.RECENT -> recent
    }

    if (list.isEmpty()) return null

    val prev = list.getOrNull(0)
    val next = list.getOrNull(1)
    val current = list.getOrNull(2)

    return Triple(prev, current, next)
}

fun isNextAllowed(nextDate: String?, date: String?): Boolean {
    if (nextDate == null || date == null) return false
    return nextDate <= date
}

/** Берём только yyyy-MM-dd из строки вида 2025-12-05T23:59:59.999999 */
private fun parseIsoDateOnlyOrNull(iso: String): LocalDate? {
    /** первые 10 символов: "2025-12-05" */
    val datePart = iso.take(10)

    return runCatching { LocalDate.parse(datePart) }.getOrNull()
}

private fun formatDateLocalized(
    date: LocalDate,
    locale: Locale,
    dateMode: DateFormatMode,
): String {
    val formatter = DateTimeFormatter.ofPattern(dateMode.pattern, locale)
    return date.format(formatter)
}

/**
 * Формирует текст описания диапазона:
 * - "Популярные посты за 5 декабря 2025"
 * - "Популярные посты с 5 декабря 2025 по 8 декабря 2025"
 */
fun formatRangeDesc(
    context: Context,
    min: String?,
    max: String?,
    dateMode: DateFormatMode,
): String {
    if (min.isNullOrBlank() || max.isNullOrBlank()) return ""

    /** локаль системы */
    val locale = context.resources.configuration.locales[0] ?: Locale.getDefault()

    val minDate = parseIsoDateOnlyOrNull(min) ?: return ""
    val maxDate = parseIsoDateOnlyOrNull(max) ?: return ""

    return if (minDate == maxDate) {
        /** одна дата */
        val formatted = formatDateLocalized(
            date = minDate,
            locale = locale,
            dateMode = dateMode,
        )
        context.getString(R.string.popular_range_single, formatted)
    } else {
        /** диапазон */
        val formattedMin = formatDateLocalized(
            date = minDate,
            locale = locale,
            dateMode = dateMode,
        )
        val formattedMax = formatDateLocalized(
            date = maxDate,
            locale = locale,
            dateMode = dateMode,
        )
        context.getString(R.string.popular_range_range, formattedMin, formattedMax)
    }
}
