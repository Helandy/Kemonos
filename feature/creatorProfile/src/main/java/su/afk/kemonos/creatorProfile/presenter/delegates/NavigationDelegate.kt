package su.afk.kemonos.creatorProfile.presenter.delegates

import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.navigation.CreatorDest
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.videoPlayer.navigation.VideoPlayerDest
import javax.inject.Inject

internal class NavigationDelegate @Inject constructor(
    private val navManager: NavigationManager,
    private val creatorPostNavigator: ICreatorPostNavigator,
) {
    /**  navigate to open funcard image */
    fun navigateToOpenImage(originalUrl: String) {
        navManager.navigate(
            VideoPlayerDest.ImageViewDest(
                imageUrl = originalUrl,
                onBack = { navManager.back() }
            )
        )
    }

    /** navigate to Link Profile */
    fun navigateToLinkProfile(creator: ProfileLink) {
        navManager.navigate(
            CreatorDest.CreatorProfile(
                service = creator.service,
                id = creator.id,
            )
        )
    }

    /** Открытие поста */
    fun navigateToPost(post: PostDomain) {
        navManager.navigate(
            creatorPostNavigator.getCreatorPostDest(
                id = post.userId,
                service = post.service,
                postId = post.id,
                showBarCreator = false
            )
        )
    }
}