package su.afk.kemonos.creatorPost.domain.model.video

import android.graphics.Bitmap

sealed interface VideoThumbState {
    data object Idle : VideoThumbState
    data object Loading : VideoThumbState
    data class Success(val bitmap: Bitmap) : VideoThumbState
    data class Error(val throwable: Throwable? = null) : VideoThumbState
}