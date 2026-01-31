package su.afk.kemonos.storage.repository.video

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_30_DAYS
import su.afk.kemonos.storage.api.repository.media.IStoreMediaInfoRepository
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.video.dao.VideoInfoDao
import javax.inject.Inject

internal class StoreMediaInfoRepository @Inject constructor(
    private val dao: VideoInfoDao
) : IStoreMediaInfoRepository {

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