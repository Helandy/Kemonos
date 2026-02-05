package su.afk.kemonos.storage.repository.postsSearch

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_1_HOURS
import su.afk.kemonos.storage.api.repository.postsSearch.IStoragePostsSearchRepository
import su.afk.kemonos.storage.entity.postsSearch.dao.CoomerPostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.KemonoPostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.mapper.PostsSearchCacheMapper

import javax.inject.Inject

internal class StoragePostsSearchRepository @Inject constructor(
    private val kemonoDao: KemonoPostsSearchCacheDao,
    private val coomerDao: CoomerPostsSearchCacheDao,
    private val mapper: PostsSearchCacheMapper,
) : IStoragePostsSearchRepository {

    override suspend fun getFreshPageOrNull(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
    ): List<PostDomain>? {
        val minTs = System.currentTimeMillis() - TTL_1_HOURS

        val rows = when (site) {
            SelectedSite.K -> kemonoDao.getFreshPage(queryKey, offset, minTs)
            SelectedSite.C -> coomerDao.getFreshPage(queryKey, offset, minTs)
        }

        return rows.takeIf { it.isNotEmpty() }?.map(mapper::toDomain)
    }

    override suspend fun getStalePageOrEmpty(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
    ): List<PostDomain> {
        val rows = when (site) {
            SelectedSite.K -> kemonoDao.getPage(queryKey, offset)
            SelectedSite.C -> coomerDao.getPage(queryKey, offset)
        }

        return rows.map(mapper::toDomain)
    }

    override suspend fun putPage(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
        items: List<PostDomain>,
    ) {
        val now = System.currentTimeMillis()

        val entities = items.mapIndexed { index, post ->
            mapper.toEntity(
                post = post,
                queryKey = queryKey,
                offset = offset,
                indexInPage = index,
                updatedAt = now
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
        val minTs = System.currentTimeMillis() - TTL_1_HOURS
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
}