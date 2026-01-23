package su.afk.kemonos.storage.repository.video

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_30_DAYS
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.video.dao.VideoInfoDao
import javax.inject.Inject

interface IStoreVideoInfoRepository {
    suspend fun get(key: String): MediaInfo?
    suspend fun upsert(key: String, info: MediaInfo)
    suspend fun clearCache()
    suspend fun clear()
}

internal class StoreVideoInfoRepository @Inject constructor(
    private val dao: VideoInfoDao
) : IStoreVideoInfoRepository {

    override suspend fun get(key: String): MediaInfo? {
        return dao.get(key)?.toDomain()
    }

    override suspend fun upsert(key: String, info: MediaInfo) {
        dao.upsert(info.toEntity(key))
    }

    override suspend fun clearCache() {
        val expireBefore = System.currentTimeMillis() - TTL_30_DAYS
        dao.clearExpired(expireBefore)
    }

    override suspend fun clear() {
        dao.clear()
    }
}