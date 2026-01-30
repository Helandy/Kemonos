package su.afk.kemonos.common.toast

private const val TOAST_FILE_NAME_MAX = 60

fun String.limitForToast(max: Int = TOAST_FILE_NAME_MAX): String =
    if (length <= max) this else take(max).trimEnd() + "…"