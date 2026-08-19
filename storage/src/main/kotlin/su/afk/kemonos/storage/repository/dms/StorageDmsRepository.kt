package su.afk.kemonos.storage.repository.dms

import su.afk.kemonos.storage.entity.dms.dao.DmsCacheDao
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.dms.DmDomain
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.storage.api.repository.dms.IStorageDmsRepository
import su.afk.kemonos.storage.entity.dms.entity.DmsCacheEntity
import javax.inject.Inject

internal class StorageDmsRepository @Inject constructor(
    private val daos: Map<SelectedSite, @JvmSuppressWildcards DmsCacheDao>,
) : IStorageDmsRepository {

    private fun dao(site: SelectedSite): DmsCacheDao = daos.getValue(site)

    override suspend fun getFreshPageOrNull(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
    ): List<DmDomain>? {
        val minTs = System.currentTimeMillis() - TTL_3_DAYS

        val rows = dao(site).getFreshPage(queryKey, offset, minTs)

        return rows.takeIf { it.isNotEmpty() }?.map(::toDomain)
    }

    override suspend fun getStalePageOrEmpty(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
    ): List<DmDomain> {
        val rows = dao(site).getPage(queryKey, offset)

        return rows.map(::toDomain)
    }

    override suspend fun putPage(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
        items: List<DmDomain>,
    ) {
        val now = System.currentTimeMillis()
        val entities = items.mapIndexed { index, item ->
            toEntity(
                item = item,
                queryKey = queryKey,
                offset = offset,
                indexInPage = index,
                updatedAt = now,
            )
        }

        dao(site).replacePage(queryKey, offset, entities)
    }

    override suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int) {
        dao(site).clearPage(queryKey, offset)
    }

    override suspend fun clearCache(site: SelectedSite) {
        val minTs = System.currentTimeMillis() - TTL_3_DAYS
        dao(site).deleteOlderThan(minTs)
    }

    override suspend fun clearAll(site: SelectedSite) {
        dao(site).clearAll()
    }

    private fun toEntity(
        item: DmDomain,
        queryKey: String,
        offset: Int,
        indexInPage: Int,
        updatedAt: Long,
    ): DmsCacheEntity = DmsCacheEntity(
        queryKey = queryKey,
        offset = offset,
        hash = item.hash,
        service = item.service,
        user = item.user,
        content = item.content,
        added = item.added,
        published = item.published,
        artistId = item.artistId,
        artistName = item.artistName,
        artistUpdated = item.artistUpdated,
        indexInPage = indexInPage,
        updatedAt = updatedAt,
    )

    private fun toDomain(entity: DmsCacheEntity): DmDomain = DmDomain(
        hash = entity.hash,
        service = entity.service,
        user = entity.user,
        content = entity.content,
        added = entity.added,
        published = entity.published,
        artistId = entity.artistId,
        artistName = entity.artistName,
        artistUpdated = entity.artistUpdated,
    )
}
