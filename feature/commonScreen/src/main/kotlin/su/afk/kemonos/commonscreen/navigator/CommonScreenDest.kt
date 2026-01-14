package su.afk.kemonos.commonscreen.navigator

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import su.afk.kemonos.domain.models.ErrorItem

object CommonScreenDest {

    @Serializable
    data class ImageViewDest(
        val imageUrl: String,
        val onBack: () -> Unit
    ) : NavKey

    @Serializable
    data class ErrorNavigatorDest(
        val error: ErrorItem
    ) : NavKey
}