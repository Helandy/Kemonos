package su.afk.kemonos.common.presenter.views.imageLoader

import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader

val LocalAppImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("ImageLoader is not provided")
}