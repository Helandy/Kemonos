package su.afk.kemonos.commonscreen.errorScreen.domain

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.commonscreen.navigator.CommonScreenDest
import su.afk.kemonos.commonscreen.navigator.IImageViewNavigator
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_IMAGE_URLS
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SELECTED_IMAGE
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SELECTED_IMAGE_INDEX
import su.afk.kemonos.navigation.storage.NavigationStorage
import javax.inject.Inject

class ImageViewNavigator @Inject constructor(
    private val navigationStorage: NavigationStorage,
) : IImageViewNavigator {
    override fun invoke(
        imageUrl: String,
        imageUrls: List<String>,
        selectedIndex: Int?,
    ): NavKey {
        val sanitizedUrls = imageUrls
            .asSequence()
            .filter { it.isNotBlank() }
            .toList()

        val galleryUrls = when {
            sanitizedUrls.isEmpty() -> listOf(imageUrl)
            imageUrl in sanitizedUrls -> sanitizedUrls
            else -> listOf(imageUrl) + sanitizedUrls
        }.distinct()

        val safeSelectedIndex = selectedIndex
            ?.takeIf { it in galleryUrls.indices }
            ?: galleryUrls.indexOf(imageUrl).takeIf { it >= 0 }
            ?: 0

        navigationStorage.put(KEY_SELECTED_IMAGE, imageUrl)
        navigationStorage.put(KEY_IMAGE_URLS, galleryUrls)
        navigationStorage.put(KEY_SELECTED_IMAGE_INDEX, safeSelectedIndex)

        return CommonScreenDest.ImageViewDest(imageUrl)
    }
}
