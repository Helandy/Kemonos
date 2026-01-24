package su.afk.kemonos.common.video

import androidx.compose.runtime.staticCompositionLocalOf
import su.afk.kemonos.storage.api.video.IVideoFrameCache

val LocalVideoFrameCache = staticCompositionLocalOf<IVideoFrameCache> {
    error("IVideoFrameCache not provided")
}