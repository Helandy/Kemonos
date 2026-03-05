package su.afk.kemonos.download.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object DownloadDestination {
    @Serializable
    data object Downloads : NavKey
}
