package su.afk.kemonos.storage.api.repository.media

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.domain.SelectedSite

interface IStorageMediaInfoRepository {
    suspend fun get(site: SelectedSite, path: String): MediaInfo?
    suspend fun upsert(site: SelectedSite, path: String, info: MediaInfo)
    suspend fun clearCache()
    suspend fun clear()
}
