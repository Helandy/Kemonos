package su.afk.kemonos.commonscreen.navigator

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import su.afk.kemonos.domain.models.ErrorItem

object CommonScreenDestination {

    @Serializable
    data class ImageViewDest(
        val args: ImageViewArgs,
    ) : NavKey

    @Serializable
    data class ImageViewArgs(
        val imageUrl: String,
        val imageUrls: List<String> = emptyList(),
        val selectedIndex: Int = 0,
        val service: String? = null,
        val creatorName: String? = null,
        val postId: String? = null,
        val postTitle: String? = null,
        val thumbnailUrls: Map<String, String> = emptyMap(),
    )

    @Serializable
    data class ErrorNavigatorDest(
        val error: ErrorItem
    ) : NavKey
}
