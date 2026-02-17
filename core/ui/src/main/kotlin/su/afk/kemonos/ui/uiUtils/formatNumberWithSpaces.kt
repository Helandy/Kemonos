package su.afk.kemonos.ui.uiUtils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun formatNumberWithSpaces(number: Int): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = ' '
    }
    val formatter = DecimalFormat("#,###", symbols)
    return formatter.format(number)
}