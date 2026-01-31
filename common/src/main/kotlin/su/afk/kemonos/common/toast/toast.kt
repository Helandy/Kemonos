package su.afk.kemonos.common.toast

import android.content.Context
import android.widget.Toast

fun Context.toast(text: CharSequence, long: Boolean = false) {
    Toast.makeText(this, text, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}