package su.afk.kemonos.creatorPost.domain.model.video

import su.afk.kemonos.creatorPost.api.domain.model.VideoInfo

sealed interface VideoInfoState {
    data object Idle : VideoInfoState
    data object Loading : VideoInfoState
    data class Success(val data: VideoInfo) : VideoInfoState
    data class Error(val throwable: Throwable) : VideoInfoState
}