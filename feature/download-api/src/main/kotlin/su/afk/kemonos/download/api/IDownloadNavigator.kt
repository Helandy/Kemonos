package su.afk.kemonos.download.api

import androidx.navigation3.runtime.NavKey

interface IDownloadNavigator {
    fun getDownloadDest(
        domain: String,
        url: String,
    ): NavKey
}