package su.afk.kemonos.creatorPost.domain.media.model

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo

internal data class CommonMediaInfo(
    val videoInfo: VideoInfo? = null,
    val mediaInfo: MediaInfo? = null,
)