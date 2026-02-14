package su.afk.kemonos.creatorPost.presenter.delegates

import su.afk.kemonos.commonscreen.navigator.IImageViewNavigator
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.navigation.NavigationManager
import javax.inject.Inject

internal class NavigateDelegates @Inject constructor(
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val imageViewNavigator: IImageViewNavigator,
) {
    fun navigateBack() {
        navManager.back()
    }

    /** навиагция на профиль автора */
    suspend fun navigateToCreatorProfile(id: String, service: String) {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = service,
                id = id
            )
        )
    }

    fun navigateOpenImage(originalUrl: String) {
        navManager.navigate(
            imageViewNavigator(
                imageUrl = originalUrl,
            )
        )
    }
}
