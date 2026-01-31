package su.afk.kemonos.storage.api.repository.media

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo

interface IStoreMediaInfoRepository {
    suspend fun get(key: String): MediaInfo?
    suspend fun upsert(key: String, info: MediaInfo)
    suspend fun clearCache()
    suspend fun clear()
}