package su.afk.kemonos.ui.date

import su.afk.kemonos.preferences.ui.DateFormatMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 2025-06-24T03:04:27.561458
 * 2025-06-22T08:34:11
 * */
private val localInputFormats = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
    "yyyy-MM-dd'T'HH:mm:ss.SSS",
    "yyyy-MM-dd'T'HH:mm:ss",
    "yyyy-MM-dd HH:mm:ss.SSS",
    "yyyy-MM-dd HH:mm:ss"
).map { DateTimeFormatter.ofPattern(it) }

private fun outputFormatter(mode: DateFormatMode): DateTimeFormatter = DateTimeFormatter.ofPattern(mode.pattern)

fun String.toUiDateTime(
    mode: DateFormatMode,
    zoneId: ZoneId = ZoneId.systemDefault(),
): String {
    val s = trim()
    if (s.isBlank()) return s

    val out = outputFormatter(mode)

    // 1) Если есть таймзона/offset -> парсим как Instant/OffsetDateTime и конвертим в local time
    if (s.endsWith("Z", ignoreCase = true) || s.contains('+') || s.lastIndexOf('-') > 9) {
        // lastIndexOf('-') > 9 — чтобы не спутать минусы в дате yyyy-MM-dd
        runCatching {
            val instant = Instant.parse(s) // работает для ...Z и ISO_INSTANT
            return LocalDateTime.ofInstant(instant, zoneId).format(out)
        }

        runCatching {
            val odt = OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            return odt.atZoneSameInstant(zoneId).toLocalDateTime().format(out)
        }
    }

    // 2) Без зоны -> LocalDateTime по паттернам
    for (fmt in localInputFormats) {
        runCatching {
            val dt = LocalDateTime.parse(s, fmt)
            return dt.format(out)
        }
    }

    return s
}

fun LocalDateTime.toUiDateTime(mode: DateFormatMode): String =
    this.format(outputFormatter(mode))

/** epoch millis -> dd.MM.yyyy */
fun Long.toUiDateTime(
    mode: DateFormatMode,
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
    return dt.format(outputFormatter(mode))
}