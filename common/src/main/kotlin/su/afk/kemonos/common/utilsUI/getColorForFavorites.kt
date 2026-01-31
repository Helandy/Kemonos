package su.afk.kemonos.common.utilsUI

import androidx.compose.ui.graphics.Color

fun getColorForFavorites(service: String): Color = when (service) {
    "patreon" -> Color(250, 87, 66)
    "fanbox" -> Color(86, 103, 124, 255)
    "discord" -> Color(81, 101, 246)
    "fantia" -> Color(255, 9, 127)
    "boosty" -> Color(253, 96, 53)
    "gumroad" -> Color(43, 159, 164)
    "subscriblestar" -> Color(0, 150, 130)
    "dlslite" -> Color(5, 42, 131)
    "onlyfans" -> Color(0, 140, 207)
    "fansly" -> Color(35, 153, 247)
    "candfans" -> Color(232, 72, 108)
    else -> Color.Green
}