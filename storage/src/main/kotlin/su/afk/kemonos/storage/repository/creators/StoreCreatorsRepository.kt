package su.afk.kemonos.storage.repository.creators

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.preferences.useCase.CacheKeys
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.CreatorsEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.creators.CreatorsEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.creators.dao.CreatorsDao
import javax.inject.Inject

internal class StoreCreatorsRepository @Inject constructor(
    private val daos: Map<SelectedSite, @JvmSuppressWildcards CreatorsDao>,
    private val cacheTimestamps: ICacheTimestampUseCase
) : IStoreCreatorsRepository {

    private fun dao(site: SelectedSite): CreatorsDao = daos.getValue(site)

    override suspend fun updateCreators(site: SelectedSite, creators: List<Creators>) {
        if (creators.isEmpty()) {
            updateCacheTimestamp(site)
            return
        }

        dao(site).replaceAllChunked(creators.map { it.toEntity() })
        updateCacheTimestamp(site)
    }

    override suspend fun clear(site: SelectedSite) {
        dao(site).clear()
        cacheTimestamps.clearCacheTimestamp(keyPref = key(site))
    }

    override suspend fun isCreatorsCacheFresh(site: SelectedSite): Boolean = isCacheFresh(site)

    override suspend fun getDistinctServices(site: SelectedSite): List<String> =
        dao(site).getDistinctServices()

    override suspend fun searchCreators(
        site: SelectedSite,
        service: String?,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<Creators> =
        dao(site)
            .search(service, query.trim(), sort, ascending, limit, offset)
            .map { it.toDomain() }

    private suspend fun CreatorsDao.search(
        service: String?,
        q: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<CreatorsEntity> = when (sort) {
        CreatorsSort.POPULARITY ->
            if (ascending) searchPopularityAsc(service, q, limit, offset)
            else searchPopularityDesc(service, q, limit, offset)

        CreatorsSort.INDEX ->
            if (ascending) searchIndexAsc(service, q, limit, offset)
            else searchIndexDesc(service, q, limit, offset)

        CreatorsSort.UPDATE ->
            if (ascending) searchUpdateAsc(service, q, limit, offset)
            else searchUpdateDesc(service, q, limit, offset)

        CreatorsSort.NAME ->
            if (ascending) searchNameAsc(service, q, limit, offset)
            else searchNameDesc(service, q, limit, offset)
    }

    override suspend fun randomCreators(
        site: SelectedSite,
        service: String?,
        limit: Int
    ): List<Creators> = dao(site).randomCreators(service, limit).map { it.toDomain() }

    override suspend fun getNamesByCompositeKeys(
        site: SelectedSite,
        compositeKeys: Set<String>
    ): Map<String, String> {
        if (compositeKeys.isEmpty()) return emptyMap()
        return dao(site).findByCompositeKeys(compositeKeys)
            .associate { "${it.service}:${it.id}" to it.name }
    }

    private fun key(site: SelectedSite): String = CacheKeys.creators(site)

    private fun getCacheTimestamp(site: SelectedSite): Long = cacheTimestamps.getCacheTimestamp(keyPref = key(site))

    private fun updateCacheTimestamp(site: SelectedSite) = cacheTimestamps.updateCacheTimestamp(keyPref = key(site))

    private fun isCacheFresh(site: SelectedSite): Boolean {
        val ts = getCacheTimestamp(site)
        if (ts == 0L) return false
        return System.currentTimeMillis() - ts < TTL_7_DAYS
    }
}
