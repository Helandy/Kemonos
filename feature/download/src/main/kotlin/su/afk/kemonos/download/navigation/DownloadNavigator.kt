package su.afk.kemonos.download.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.download.api.IDownloadNavigator
import javax.inject.Inject

class DownloadNavigator @Inject constructor() : IDownloadNavigator {
    override fun getDownloadDest(domain: String, url: String): NavKey {

        return DownloadDest.Download(
            domain = domain,
            url = url
        )
    }
}