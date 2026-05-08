package su.afk.kemonos.commonscreen.errorScreen.domain

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.commonscreen.navigator.CommonScreenDestination
import su.afk.kemonos.commonscreen.navigator.IImageViewNavigator
import javax.inject.Inject

class ImageViewNavigator @Inject constructor() : IImageViewNavigator {
    override fun invoke(
        imageUrl: String,
        imageUrls: List<String>,
        selectedIndex: Int?,
        service: String?,
        creatorName: String?,
        postId: String?,
        postTitle: String?,
        thumbnailUrls: Map<String, String>,
    ): NavKey {
        val sanitizedUrls = imageUrls
            .asSequence()
            .filter { it.isNotBlank() }
            .toList()

        val galleryUrls = when {
            sanitizedUrls.isEmpty() -> listOf(imageUrl)
            imageUrl in sanitizedUrls -> sanitizedUrls
            else -> listOf(imageUrl) + sanitizedUrls
        }.distinct()

        val safeSelectedIndex = selectedIndex
            ?.takeIf { it in galleryUrls.indices }
            ?: galleryUrls.indexOf(imageUrl).takeIf { it >= 0 }
            ?: 0

        return CommonScreenDestination.ImageViewDest(
            args = CommonScreenDestination.ImageViewArgs(
                imageUrl = imageUrl,
                imageUrls = galleryUrls,
                selectedIndex = safeSelectedIndex,
                service = service,
                creatorName = creatorName,
                postId = postId,
                postTitle = postTitle,
                thumbnailUrls = thumbnailUrls,
            )
        )
    }
}
