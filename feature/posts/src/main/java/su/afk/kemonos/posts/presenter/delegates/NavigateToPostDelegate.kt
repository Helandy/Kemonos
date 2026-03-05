package su.afk.kemonos.posts.presenter.delegates

import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.posts.domain.usecase.GetRandomPost
import javax.inject.Inject

internal class NavigateToPostDelegate @Inject constructor(
    private val getRandomPost: GetRandomPost,
    private val creatorPostNavigator: ICreatorPostNavigator,
    private val navManager: NavigationManager,
) {

    /** Открытие поста */
    suspend fun navigateToPost(post: PostDomain) {
        navManager.navigate(
            creatorPostNavigator.getCreatorPostDest(
                id = post.userId,
                service = post.service,
                postId = post.id,
                showBarCreator = true
            )
        )
    }

    suspend fun navigateToRandomPost() {
        val randomPost = getRandomPost()

        navManager.navigate(
            creatorPostNavigator.getCreatorPostDest(
                id = randomPost.artistId,
                service = randomPost.service,
                postId = randomPost.postId,
                showBarCreator = true
            )
        )
    }
}
