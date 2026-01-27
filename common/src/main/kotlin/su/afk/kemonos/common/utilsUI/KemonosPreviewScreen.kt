package su.afk.kemonos.common.utilsUI

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.di.PreviewDomainResolver
import su.afk.kemonos.common.error.LocalErrorMapper
import su.afk.kemonos.common.error.PreviewErrorMapper
import su.afk.kemonos.common.imageLoader.LocalAppImageLoader
import su.afk.kemonos.common.imageLoader.previewImageLoader
import su.afk.kemonos.common.video.LocalVideoFrameCache
import su.afk.kemonos.common.video.PreviewVideoFrameCache

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