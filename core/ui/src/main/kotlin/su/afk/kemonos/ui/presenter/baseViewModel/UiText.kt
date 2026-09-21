package su.afk.kemonos.ui.presenter.baseViewModel

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** A presentation-layer string that can be localized or supplied dynamically. */
sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    data class Resource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText
}

fun UiText.asString(context: Context): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.Resource -> context.getString(id, *args.toTypedArray())
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.Resource -> stringResource(id, *args.toTypedArray())
}
