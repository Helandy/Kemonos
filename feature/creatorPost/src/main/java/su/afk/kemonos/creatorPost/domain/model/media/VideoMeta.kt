package su.afk.kemonos.creatorPost.domain.model.media

import android.graphics.Bitmap
import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo

data class VideoMeta(
    val info: MediaInfo,
    val frame: Bitmap?
)