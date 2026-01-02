package su.afk.kemonos.storage.repository.tags

import su.afk.kemonos.api.domain.tags.Tags
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheTimestampUseCase
import su.afk.kemonos.storage.entity.tags.TagsEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.tags.TagsEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.tags.dao.CoomerTagsDao
import su.afk.kemonos.storage.entity.tags.dao.KemonoTagsDao
import javax.inject.Inject

interface IStoreTagsRepository {
    suspend fun getAll(site: SelectedSite): List<Tags>
    suspend fun update(site: SelectedSite, items: List<Tags>)
    suspend fun clear(site: SelectedSite)
    suspend fun isCacheFresh(site: SelectedSite): Boolean
    suspend fun clearIfExpired(site: SelectedSite)
}

internal class StoreTagsRepository @Inject constructor(
    private val kemonoTagsDao: KemonoTagsDao,
    private val coomerTagsDao: CoomerTagsDao,
    private val cacheTimestamps: CacheTimestampUseCase,
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
        return System.currentTimeMillis() - ts < CACHE_TTL_MS
    }

    private fun key(site: SelectedSite): String =
        if (site == SelectedSite.K) KEY_KEMONO else KEY_COOMER

    private companion object {
        private const val CACHE_TTL_MS = 30L * 24 * 60 * 60 * 1000 // 30 дней
        private const val KEY_KEMONO = "kemono_tags_cache_time"
        private const val KEY_COOMER = "coomer_tags_cache_time"
    }
}