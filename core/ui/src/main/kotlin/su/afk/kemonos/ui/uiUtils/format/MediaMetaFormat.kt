package su.afk.kemonos.ui.uiUtils.format

import java.util.*

fun formatDurationMinutesSeconds(durationMs: Long): String? {
    if (durationMs <= 0L) return null

    val totalSeconds = durationMs / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L

    return when {
        hours > 0L -> "%d:%02d:%02d".format(hours, minutes, seconds)
        minutes > 0L -> "%d:%02d".format(minutes, seconds)
        else -> seconds.toString()
    }
}

fun formatSizeMegabytesRounded(sizeBytes: Long): String? {
    if (sizeBytes < 0L) return null

    val oneGigabyte = 1024L * 1024L * 1024L
    if (sizeBytes < oneGigabyte) {
        val sizeMb = sizeBytes / 1024.0 / 1024.0
        return formatDecimal(sizeMb) + " MB"
    }

    val sizeGb = sizeBytes / 1024.0 / 1024.0 / 1024.0
    return formatDecimal(sizeGb) + " GB"
}

private fun formatDecimal(value: Double): String =
    String.format(Locale.US, "%.1f", value).removeSuffix(".0")
