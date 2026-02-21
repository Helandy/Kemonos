package su.afk.kemonos.creatorPost.presenter.delegates

import su.afk.kemonos.commonscreen.navigator.IImageViewNavigator
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.models.Tag
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

    suspend fun navigateToCreatorProfileByTag(id: String, service: String, tag: String) {
        val selectedTag = Tag(tag = tag, postCount = 0)
        val previousIndex = navManager.backStack.lastIndex - 1
        val previousDest = navManager.backStack.getOrNull(previousIndex)
        val creatorProfileDest = creatorProfileNavigator.getCreatorProfileDest(
            service = service,
            id = id,
            tag = selectedTag
        )

        if (previousDest?.javaClass?.name == CREATOR_PROFILE_DEST_CLASS_NAME) {
            navManager.backStack[previousIndex] = creatorProfileDest
            navManager.back()
            return
        }

        navManager.navigate(creatorProfileDest)
    }

    fun navigateOpenImage(
        originalUrl: String,
        imageUrls: List<String> = emptyList(),
        selectedIndex: Int? = null,
        service: String? = null,
        creatorName: String? = null,
        postId: String? = null,
        postTitle: String? = null,
    ) {
        navManager.navigate(
            imageViewNavigator(
                imageUrl = originalUrl,
                imageUrls = imageUrls,
                selectedIndex = selectedIndex,
                service = service,
                creatorName = creatorName,
                postId = postId,
                postTitle = postTitle,
            )
        )
    }

    private companion object {
        const val CREATOR_PROFILE_DEST_CLASS_NAME =
            "su.afk.kemonos.creatorProfile.navigation.CreatorDest\$CreatorProfile"
    }
}
