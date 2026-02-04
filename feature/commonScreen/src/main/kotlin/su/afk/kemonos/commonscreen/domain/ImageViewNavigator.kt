package su.afk.kemonos.commonscreen.domain

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.commonscreen.navigator.CommonScreenDest
import su.afk.kemonos.commonscreen.navigator.IImageViewNavigator
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SELECTED_IMAGE
import su.afk.kemonos.navigation.storage.NavigationStorage
import javax.inject.Inject

class ImageViewNavigator @Inject constructor(
    private val navigationStorage: NavigationStorage,
) : IImageViewNavigator {
    override fun invoke(imageUrl: String): NavKey {

        navigationStorage.put(KEY_SELECTED_IMAGE, imageUrl)

        return CommonScreenDest.ImageViewDest(imageUrl)
    }
}