package su.afk.kemonos.common.presenter.utilsUI

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.di.PreviewDomainResolver
import su.afk.kemonos.common.presenter.views.imageLoader.LocalAppImageLoader
import su.afk.kemonos.common.presenter.views.imageLoader.previewImageLoader

/** Универсальный превьюер с мок зависимостями */
@Composable
fun PreviewScreen(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    CompositionLocalProvider(
        LocalDomainResolver provides PreviewDomainResolver,
        LocalAppImageLoader provides previewImageLoader(context),
    ) {
        MaterialTheme {
            content()
        }
    }
}