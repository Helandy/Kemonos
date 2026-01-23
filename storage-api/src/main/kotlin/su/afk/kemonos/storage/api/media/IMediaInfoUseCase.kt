package su.afk.kemonos.storage.api.media

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo

interface IMediaInfoUseCase {
    suspend fun get(key: String): MediaInfo?
    suspend fun upsert(key: String, info: MediaInfo)
    suspend fun clear()
}