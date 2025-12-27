package su.afk.kemonos.videoPlayer.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


object VideoPlayerDest {
    @Serializable
    data class ImageViewDest(
        val imageUrl: String,
        val onBack: () -> Unit
    ) : NavKey
}