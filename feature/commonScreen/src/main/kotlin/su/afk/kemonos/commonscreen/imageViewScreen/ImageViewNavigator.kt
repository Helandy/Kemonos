package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.commonscreen.navigator.CommonScreenDest
import su.afk.kemonos.commonscreen.navigator.IImageViewNavigator
import javax.inject.Inject

class ImageViewNavigator @Inject constructor() : IImageViewNavigator {
    override fun invoke(imageUrl: String, onBack: () -> Unit): NavKey = CommonScreenDest.ImageViewDest(imageUrl, onBack)
}