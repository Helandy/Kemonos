package su.afk.kemonos.ui.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import su.afk.kemonos.error.error.LocalErrorMapper
import su.afk.kemonos.error.error.PreviewErrorMapper
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.preferences.domainResolver.PreviewDomainResolver
import su.afk.kemonos.ui.imageLoader.LocalAppImageLoader
import su.afk.kemonos.ui.imageLoader.previewImageLoader
import su.afk.kemonos.ui.video.LocalVideoFrameCache
import su.afk.kemonos.ui.video.PreviewVideoFrameCache

/** Универсальный превьюер с мок зависимостями */
@Composable
fun KemonosPreviewScreen(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    CompositionLocalProvider(
        LocalDomainResolver provides PreviewDomainResolver,
        LocalAppImageLoader provides previewImageLoader(context),
        LocalErrorMapper provides PreviewErrorMapper,
        LocalVideoFrameCache provides PreviewVideoFrameCache,
    ) {
        MaterialTheme {
            content()
        }
    }
}