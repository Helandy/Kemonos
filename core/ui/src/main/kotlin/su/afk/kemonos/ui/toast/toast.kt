package su.afk.kemonos.ui.toast

import android.content.Context
import android.widget.Toast

private const val TOAST_FILE_NAME_MAX = 60

fun CharSequence.limitForToast(max: Int = TOAST_FILE_NAME_MAX): String {
    val value = toString()
    return if (value.length <= max) value else value.take(max).trimEnd() + "…"
}

fun Context.toast(text: CharSequence, long: Boolean = false) {
    Toast.makeText(
        this,
        text.limitForToast(),
        if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}
