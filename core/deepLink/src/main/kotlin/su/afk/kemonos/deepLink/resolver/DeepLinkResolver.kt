package su.afk.kemonos.deepLink.resolver

import android.net.Uri
import androidx.navigation3.runtime.NavKey

interface DeepLinkResolver {
    suspend fun resolve(uri: Uri): NavKey?
}