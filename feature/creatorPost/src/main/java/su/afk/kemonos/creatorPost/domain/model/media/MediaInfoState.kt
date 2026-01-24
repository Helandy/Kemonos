package su.afk.kemonos.creatorPost.domain.model.media

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo

sealed interface MediaInfoState {
    data object Idle : MediaInfoState
    data object Loading : MediaInfoState
    data class Success(val data: MediaInfo) : MediaInfoState
    data class Error(val throwable: Throwable) : MediaInfoState
}