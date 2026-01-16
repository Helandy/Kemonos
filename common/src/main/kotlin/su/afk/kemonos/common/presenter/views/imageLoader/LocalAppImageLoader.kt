package su.afk.kemonos.common.presenter.views.imageLoader

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader
import coil3.request.crossfade

val LocalAppImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("ImageLoader is not provided")
}

/** Для превью */
internal fun previewImageLoader(context: Context): ImageLoader =
    ImageLoader.Builder(context)
        .crossfade(true)
        .build()