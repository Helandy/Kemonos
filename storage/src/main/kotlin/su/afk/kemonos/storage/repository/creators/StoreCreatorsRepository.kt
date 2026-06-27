package su.afk.kemonos.storage.repository.creators

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.preferences.useCase.CacheKeys.CREATORS_COOMER
import su.afk.kemonos.preferences.useCase.CacheKeys.CREATORS_KEMONO
import su.afk.kemonos.preferences.useCase.CacheKeys.CREATORS_PAWCHIVE
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.CreatorsEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.creators.CreatorsEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.creators.dao.CoomerCreatorsDao
import su.afk.kemonos.storage.entity.creators.dao.KemonoCreatorsDao
import su.afk.kemonos.storage.entity.creators.dao.PawchiveCreatorsDao
import javax.inject.Inject

internal class StoreCreatorsRepository @Inject constructor(
    private val kemonoDao: KemonoCreatorsDao,
    private val coomerDao: CoomerCreatorsDao,
    private val pawchiveDao: PawchiveCreatorsDao,
    private val cacheTimestamps: ICacheTimestampUseCase
) : IStoreCreatorsRepository {

    override suspend fun updateCreators(site: SelectedSite, creators: List<Creators>) {
        if (creators.isEmpty()) {
            updateCacheTimestamp(site)
            return
        }

        val entities = creators.map { it.toEntity() }
        when (site) {
            SelectedSite.K -> {
                kemonoDao.replaceAllChunked(entities)
            }
            SelectedSite.C -> {
                coomerDao.replaceAllChunked(entities)
            }
            SelectedSite.P -> {
                pawchiveDao.replaceAllChunked(entities)
            }
        }
        updateCacheTimestamp(site)
    }

    override suspend fun clear(site: SelectedSite) {
        when (site) {
            SelectedSite.K -> kemonoDao.clear()
            SelectedSite.C -> coomerDao.clear()
            SelectedSite.P -> pawchiveDao.clear()
        }
        cacheTimestamps.clearCacheTimestamp(keyPref = key(site))
    }

    override suspend fun isCreatorsCacheFresh(site: SelectedSite): Boolean = isCacheFresh(site)

    override suspend fun getDistinctServices(site: SelectedSite): List<String> =
        when (site) {
            SelectedSite.K -> kemonoDao.getDistinctServices()
            SelectedSite.C -> coomerDao.getDistinctServices()
            SelectedSite.P -> pawchiveDao.getDistinctServices()
        }

    override suspend fun searchCreators(
        site: SelectedSite,
        service: String?,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<Creators> {
        val q = query.trim()

        return when (site) {
            SelectedSite.K -> searchKemono(service, q, sort, ascending, limit, offset).map { it.toDomain() }
            SelectedSite.C -> searchCoomer(service, q, sort, ascending, limit, offset).map { it.toDomain() }
            SelectedSite.P -> searchPawchive(service, q, sort, ascending, limit, offset).map { it.toDomain() }
        }
    }

    private suspend fun searchKemono(
        service: String?,
        q: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<CreatorsEntity> = when (sort) {
        CreatorsSort.POPULARITY ->
            if (ascending) kemonoDao.searchPopularityAsc(service, q, limit, offset)
            else kemonoDao.searchPopularityDesc(service, q, limit, offset)

        CreatorsSort.INDEX ->
            if (ascending) kemonoDao.searchIndexAsc(service, q, limit, offset)
            else kemonoDao.searchIndexDesc(service, q, limit, offset)

        CreatorsSort.UPDATE ->
            if (ascending) kemonoDao.searchUpdateAsc(service, q, limit, offset)
            else kemonoDao.searchUpdateDesc(service, q, limit, offset)

        CreatorsSort.NAME ->
            if (ascending) kemonoDao.searchNameAsc(service, q, limit, offset)
            else kemonoDao.searchNameDesc(service, q, limit, offset)
    }

    private suspend fun searchCoomer(
        service: String?,
        q: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<CreatorsEntity> = when (sort) {
        CreatorsSort.POPULARITY ->
            if (ascending) coomerDao.searchPopularityAsc(service, q, limit, offset)
            else coomerDao.searchPopularityDesc(service, q, limit, offset)

        CreatorsSort.INDEX ->
            if (ascending) coomerDao.searchIndexAsc(service, q, limit, offset)
            else coomerDao.searchIndexDesc(service, q, limit, offset)

        CreatorsSort.UPDATE ->
            if (ascending) coomerDao.searchUpdateAsc(service, q, limit, offset)
            else coomerDao.searchUpdateDesc(service, q, limit, offset)

        CreatorsSort.NAME ->
            if (ascending) coomerDao.searchNameAsc(service, q, limit, offset)
            else coomerDao.searchNameDesc(service, q, limit, offset)
    }

    private suspend fun searchPawchive(
        service: String?,
        q: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<CreatorsEntity> = when (sort) {
        CreatorsSort.POPULARITY ->
            if (ascending) pawchiveDao.searchPopularityAsc(service, q, limit, offset)
            else pawchiveDao.searchPopularityDesc(service, q, limit, offset)

        CreatorsSort.INDEX ->
            if (ascending) pawchiveDao.searchIndexAsc(service, q, limit, offset)
            else pawchiveDao.searchIndexDesc(service, q, limit, offset)

        CreatorsSort.UPDATE ->
            if (ascending) pawchiveDao.searchUpdateAsc(service, q, limit, offset)
            else pawchiveDao.searchUpdateDesc(service, q, limit, offset)

        CreatorsSort.NAME ->
            if (ascending) pawchiveDao.searchNameAsc(service, q, limit, offset)
            else pawchiveDao.searchNameDesc(service, q, limit, offset)
    }

    override suspend fun randomCreators(
        site: SelectedSite,
        service: String?,
        limit: Int
    ): List<Creators> = when (site) {
        SelectedSite.K -> kemonoDao.randomCreators(service, limit).map { it.toDomain() }
        SelectedSite.C -> coomerDao.randomCreators(service, limit).map { it.toDomain() }
        SelectedSite.P -> pawchiveDao.randomCreators(service, limit).map { it.toDomain() }
    }

    override suspend fun getNamesByCompositeKeys(
        site: SelectedSite,
        compositeKeys: Set<String>
    ): Map<String, String> {
        if (compositeKeys.isEmpty()) return emptyMap()
        val items = when (site) {
            SelectedSite.K -> kemonoDao.findByCompositeKeys(compositeKeys)
            SelectedSite.C -> coomerDao.findByCompositeKeys(compositeKeys)
            SelectedSite.P -> pawchiveDao.findByCompositeKeys(compositeKeys)
        }
        return items.associate { "${it.service}:${it.id}" to it.name }
    }

    private fun key(site: SelectedSite): String = when (site) {
        SelectedSite.K -> CREATORS_KEMONO
        SelectedSite.C -> CREATORS_COOMER
        SelectedSite.P -> CREATORS_PAWCHIVE
    }

    private fun getCacheTimestamp(site: SelectedSite): Long = cacheTimestamps.getCacheTimestamp(keyPref = key(site))

    private fun updateCacheTimestamp(site: SelectedSite) = cacheTimestamps.updateCacheTimestamp(keyPref = key(site))

    private fun isCacheFresh(site: SelectedSite): Boolean {
        val ts = getCacheTimestamp(site)
        if (ts == 0L) return false
        return System.currentTimeMillis() - ts < TTL_7_DAYS
    }
}
