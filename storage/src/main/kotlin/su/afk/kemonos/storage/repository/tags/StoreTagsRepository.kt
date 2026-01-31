package su.afk.kemonos.storage.repository.tags

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.preferences.useCase.CacheKeys.TAGS_COOMER
import su.afk.kemonos.preferences.useCase.CacheKeys.TAGS_KEMONO
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_30_DAYS
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.storage.api.repository.tags.IStoreTagsRepository
import su.afk.kemonos.storage.entity.tags.TagsEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.tags.TagsEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.tags.dao.CoomerTagsDao
import su.afk.kemonos.storage.entity.tags.dao.KemonoTagsDao
import javax.inject.Inject

internal class StoreTagsRepository @Inject constructor(
    private val kemonoTagsDao: KemonoTagsDao,
    private val coomerTagsDao: CoomerTagsDao,
    private val cacheTimestamps: ICacheTimestampUseCase,
) : IStoreTagsRepository {

    override suspend fun getAll(site: SelectedSite): List<Tags> {
        return when (site) {
            SelectedSite.K -> kemonoTagsDao.getAll().map { it.toDomain() }
            SelectedSite.C -> coomerTagsDao.getAll().map { it.toDomain() }
        }
    }

    override suspend fun update(site: SelectedSite, items: List<Tags>) {
        when (site) {
            SelectedSite.K -> kemonoTagsDao.replaceAll(items.map { it.toEntity() })
            SelectedSite.C -> coomerTagsDao.replaceAll(items.map { it.toEntity() })
        }
        updateCacheTimestamp(site)
    }

    override suspend fun clear(site: SelectedSite) {
        when (site) {
            SelectedSite.K -> kemonoTagsDao.clear()
            SelectedSite.C -> coomerTagsDao.clear()
        }
        cacheTimestamps.clearCacheTimestamp(keyPref = key(site))
    }

    override suspend fun isCacheFresh(site: SelectedSite): Boolean = isCacheFreshInternal(site)

    override suspend fun clearIfExpired(site: SelectedSite) {
        if (!isCacheFresh(site)) {
            clear(site)
        }
    }

    private fun updateCacheTimestamp(site: SelectedSite) {
        cacheTimestamps.updateCacheTimestamp(keyPref = key(site))
    }

    private fun getCacheTimestamp(site: SelectedSite): Long =
        cacheTimestamps.getCacheTimestamp(keyPref = key(site))

    private fun isCacheFreshInternal(site: SelectedSite): Boolean {
        val ts = getCacheTimestamp(site)
        if (ts == 0L) return false
        return System.currentTimeMillis() - ts < TTL_30_DAYS
    }

    private fun key(site: SelectedSite): String =
        if (site == SelectedSite.K) TAGS_KEMONO else TAGS_COOMER
}