package su.afk.kemonos.videoPlayer.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.videoPlayer.presenter.imageView.ImagePreviewScreen

class VideoPlayerRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<VideoPlayerDest.ImageViewDest> { backStackEntry ->
            ImagePreviewScreen(
                imageUrl = backStackEntry.imageUrl,
                onBack = backStackEntry.onBack,
            )
        }
    }
}