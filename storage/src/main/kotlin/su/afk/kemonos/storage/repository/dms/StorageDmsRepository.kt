package su.afk.kemonos.storage.repository.dms

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.storage.api.repository.dms.IStorageDmsRepository
import su.afk.kemonos.storage.api.repository.dms.StorageDmItem
import su.afk.kemonos.storage.entity.dms.dao.CoomerDmsCacheDao
import su.afk.kemonos.storage.entity.dms.dao.KemonoDmsCacheDao
import su.afk.kemonos.storage.entity.dms.entity.DmsCacheEntity
import javax.inject.Inject

internal class StorageDmsRepository @Inject constructor(
    private val kemonoDao: KemonoDmsCacheDao,
    private val coomerDao: CoomerDmsCacheDao,
) : IStorageDmsRepository {

    override suspend fun getFreshPageOrNull(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
    ): List<StorageDmItem>? {
        val minTs = System.currentTimeMillis() - TTL_3_DAYS

        val rows = when (site) {
            SelectedSite.K -> kemonoDao.getFreshPage(queryKey, offset, minTs)
            SelectedSite.C -> coomerDao.getFreshPage(queryKey, offset, minTs)
        }

        return rows.takeIf { it.isNotEmpty() }?.map(::toStorageItem)
    }

    override suspend fun putPage(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
        items: List<StorageDmItem>,
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

        when (site) {
            SelectedSite.K -> kemonoDao.replacePage(queryKey, offset, entities)
            SelectedSite.C -> coomerDao.replacePage(queryKey, offset, entities)
        }
    }

    override suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int) {
        when (site) {
            SelectedSite.K -> kemonoDao.clearPage(queryKey, offset)
            SelectedSite.C -> coomerDao.clearPage(queryKey, offset)
        }
    }

    override suspend fun clearCache(site: SelectedSite) {
        val minTs = System.currentTimeMillis() - TTL_3_DAYS
        when (site) {
            SelectedSite.K -> kemonoDao.deleteOlderThan(minTs)
            SelectedSite.C -> coomerDao.deleteOlderThan(minTs)
        }
    }

    override suspend fun clearAll(site: SelectedSite) {
        when (site) {
            SelectedSite.K -> kemonoDao.clearAll()
            SelectedSite.C -> coomerDao.clearAll()
        }
    }

    private fun toEntity(
        item: StorageDmItem,
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

    private fun toStorageItem(entity: DmsCacheEntity): StorageDmItem = StorageDmItem(
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
