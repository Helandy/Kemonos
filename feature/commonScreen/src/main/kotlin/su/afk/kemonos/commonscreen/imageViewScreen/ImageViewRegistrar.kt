package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.commonscreen.navigator.CommonScreenDest
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import javax.inject.Inject

class ImageViewRegistrar @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CommonScreenDest.ImageViewDest> { backStackEntry ->
            ImageViewScreen(
                imageUrl = backStackEntry.imageUrl,
                onBack = backStackEntry.onBack,
            )
        }
    }
}