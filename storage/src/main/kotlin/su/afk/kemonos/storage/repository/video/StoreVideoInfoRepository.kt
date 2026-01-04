package su.afk.kemonos.storage.repository.video

import su.afk.kemonos.creatorPost.api.domain.model.VideoInfo
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_30_DAYS
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.video.dao.VideoInfoDao
import javax.inject.Inject

interface IStoreVideoInfoRepository {
    suspend fun get(name: String): VideoInfo?
    suspend fun upsert(name: String, info: VideoInfo)
    suspend fun clearCache()
    suspend fun clear()
}

internal class StoreVideoInfoRepository @Inject constructor(
    private val dao: VideoInfoDao
) : IStoreVideoInfoRepository {

    override suspend fun get(name: String): VideoInfo? {
        return dao.get(name)?.toDomain()
    }

    override suspend fun upsert(name: String, info: VideoInfo) {
        dao.upsert(info.toEntity(name))
    }

    override suspend fun clearCache() {
        val expireBefore = System.currentTimeMillis() - TTL_30_DAYS
        dao.clearExpired(expireBefore)
    }

    override suspend fun clear() {
        dao.clear()
    }
}