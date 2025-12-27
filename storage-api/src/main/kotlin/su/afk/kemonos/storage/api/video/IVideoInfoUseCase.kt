package su.afk.kemonos.storage.api.video

import su.afk.kemonos.creatorPost.api.domain.model.VideoInfo

interface IVideoInfoUseCase {
    suspend fun get(name: String): VideoInfo?
    suspend fun upsert(name: String, info: VideoInfo)
    suspend fun clear()
}