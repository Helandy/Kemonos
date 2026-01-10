package su.afk.kemonos.download.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object DownloadDest {

    @Serializable
    data class Download(
        val domain: String,
        val url: String,
    ) : NavKey
}