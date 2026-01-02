package su.afk.kemonos.posts.presenter.common

import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.navigation.NavigationManager
import javax.inject.Inject

internal class NavigateToPostDelegate @Inject constructor(
    private val creatorPostNavigator: ICreatorPostNavigator,
    private val navManager: NavigationManager,
) {

    /** Открытие поста */
    fun navigateToPost(post: PostDomain) {
        navManager.navigate(
            creatorPostNavigator.getCreatorPostDest(
                id = post.userId,
                service = post.service,
                postId = post.id,
                showBarCreator = true
            )
        )
    }

    fun navigateToPostId(service: String, userId: String, postId: String) {
        navManager.navigate(
            creatorPostNavigator.getCreatorPostDest(
                service = service,
                id = userId,
                postId = postId,
                showBarCreator = true
            )
        )
    }
}