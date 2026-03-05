package su.afk.kemonos.ui.uiUtils.format

import kotlin.math.roundToInt

fun formatDurationMinutesSeconds(durationMs: Long): String? {
    if (durationMs <= 0L) return null

    val totalSeconds = durationMs / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L

    return "%d:%02d".format(minutes, seconds)
}

fun formatSizeMegabytesRounded(sizeBytes: Long): String? {
    if (sizeBytes < 0L) return null

    val sizeMb = sizeBytes / 1024f / 1024f
    return "${sizeMb.roundToInt()} MB"
}
