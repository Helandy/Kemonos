package su.afk.kemonos.creatorPost.domain.media.model

import su.afk.kemonos.domain.models.media.MediaInfo
import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo

internal data class CommonMediaInfo(
    val videoInfo: VideoInfo? = null,
    val mediaInfo: MediaInfo? = null,
)