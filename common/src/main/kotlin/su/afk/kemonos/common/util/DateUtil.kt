package su.afk.kemonos.common.util

import java.time.LocalDateTime
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

private val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun String.toUiDateTime(): String {
    for (pattern in inputFormats) {
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val dateTime = LocalDateTime.parse(this, formatter)
            return dateTime.format(outputFormatter)
        } catch (_: Exception) {
        }
    }
    return this
}

fun LocalDateTime.toUiDateTime(): String {
    return this.format(outputFormatter)
}