package su.afk.kemonos.ui.uiUtils.size

fun formatBytes(v: Long): String = when {
    v < 1024 -> "$v B"
    v < 1024 * 1024 -> "%.1f KB".format(v / 1024f)
    v < 1024 * 1024 * 1024 -> "%.1f MB".format(v / 1024f / 1024f)
    else -> "%.2f GB".format(v / 1024f / 1024f / 1024f)
}