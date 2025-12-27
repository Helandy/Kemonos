package su.afk.kemonos.creatorPost.presenter.delegates

import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.videoPlayer.navigation.VideoPlayerDest
import javax.inject.Inject

internal class NavigateDelegates @Inject constructor(
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
) {
    /** навиагция на профиль автора */
    fun navigateToCreatorProfile(id: String, service: String) {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = service,
                id = id
            )
        )
    }

    fun navigateOpenImage(originalUrl: String) {
        navManager.navigate(
            VideoPlayerDest.ImageViewDest(
                imageUrl = originalUrl,
                onBack = { navManager.back() }
            )
        )
    }
}