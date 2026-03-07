package su.afk.kemonos.storage.repository.video

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_30_DAYS
import su.afk.kemonos.storage.api.repository.media.IStorageMediaInfoRepository
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.video.VideoInfoEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.video.dao.VideoInfoDao
import javax.inject.Inject

internal class StoreMediaInfoRepository @Inject constructor(
    private val dao: VideoInfoDao
) : IStorageMediaInfoRepository {

    override suspend fun get(site: SelectedSite, path: String): MediaInfo? {
        return dao.get(site, path)?.toDomain()
    }

    override suspend fun upsert(site: SelectedSite, path: String, info: MediaInfo) {
        dao.upsert(info.toEntity(site, path))
    }

    override suspend fun clearCache() {
        val expireBefore = System.currentTimeMillis() - TTL_30_DAYS
        dao.clearExpired(expireBefore)
    }

    override suspend fun clear() {
        dao.clear()
    }
}
