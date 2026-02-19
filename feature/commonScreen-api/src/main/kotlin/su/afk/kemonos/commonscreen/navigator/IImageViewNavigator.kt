package su.afk.kemonos.commonscreen.navigator

import androidx.navigation3.runtime.NavKey

interface IImageViewNavigator {
    operator fun invoke(
        imageUrl: String,
        imageUrls: List<String> = emptyList(),
        selectedIndex: Int? = null,
    ): NavKey
}
