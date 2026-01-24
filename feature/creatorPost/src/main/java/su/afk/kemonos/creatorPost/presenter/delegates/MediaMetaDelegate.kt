package su.afk.kemonos.creatorPost.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import su.afk.kemonos.creatorPost.domain.model.media.VideoMeta
import su.afk.kemonos.creatorPost.domain.useCase.GetMediaMetaUseCase

internal class MediaMetaDelegate(
    private val scope: CoroutineScope,
    private val getMediaMeta: GetMediaMetaUseCase,
    private val timeoutMs: Long = 30_000L,
) {
    private val jobs = mutableMapOf<String, Job>()

    private fun launchOnce(key: String, block: suspend () -> Unit) {
        if (jobs[key]?.isActive == true) return
        jobs[key] = scope.launch {
            try {
                withTimeout(timeoutMs) { block() }
            } finally {
                jobs.remove(key)
            }
        }
    }

    fun requestVideo(
        url: String,
        onSuccess: (VideoMeta) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        launchOnce(key = "video:$url") {
            runCatching {
                getMediaMeta(
                    url = url,
                    loadFrame = true,
                )
            }.onSuccess(onSuccess)
                .onFailure(onError)
        }
    }

    fun requestAudio(
        url: String,
        onSuccess: (VideoMeta) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        launchOnce(key = "audio:$url") {
            runCatching {
                getMediaMeta(
                    url = url,
                    loadFrame = false,
                )
            }.onSuccess(onSuccess)
                .onFailure(onError)
        }
    }
}