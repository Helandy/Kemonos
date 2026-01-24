package su.afk.kemonos.common.util

import su.afk.kemonos.preferences.ui.DateFormatMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 2025-06-24T03:04:27.561458
 * 2025-06-22T08:34:11
 * */
private val inputFormats = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
    "yyyy-MM-dd'T'HH:mm:ss.SSS",
    "yyyy-MM-dd'T'HH:mm:ss",
    "yyyy-MM-dd HH:mm:ss.SSS",
    "yyyy-MM-dd HH:mm:ss"
)

private fun outputFormatter(mode: DateFormatMode): DateTimeFormatter = DateTimeFormatter.ofPattern(mode.pattern)

fun String.toUiDateTime(mode: DateFormatMode): String {
    val out = outputFormatter(mode)
    for (pattern in inputFormats) {
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val dateTime = LocalDateTime.parse(this, formatter)
            return dateTime.format(out)
        } catch (_: Exception) {
        }
    }
    return this
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