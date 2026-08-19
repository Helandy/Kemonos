package su.afk.kemonos.storage.repository.postsSearch

import su.afk.kemonos.storage.entity.postsSearch.dao.PostsSearchCacheDao
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.useCase.CacheTimes.TLL_1_DAYS
import su.afk.kemonos.storage.api.repository.postsSearch.IStoragePostsSearchRepository
import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity
import su.afk.kemonos.storage.entity.postsSearch.mapper.PostsSearchCacheMapper
import javax.inject.Inject

internal class StoragePostsSearchRepository @Inject constructor(
    private val daos: Map<SelectedSite, @JvmSuppressWildcards PostsSearchCacheDao>,
    private val mapper: PostsSearchCacheMapper,
) : IStoragePostsSearchRepository {

    private fun dao(site: SelectedSite): PostsSearchCacheDao = daos.getValue(site)
    private companion object {
        const val EMPTY_MARKER_ID = "__empty_cache_page__"
        const val EMPTY_MARKER_USER_ID = "__meta__"
        const val EMPTY_MARKER_SERVICE = "__meta__"
    }

    override suspend fun getFreshPageOrNull(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
    ): List<PostDomain>? {
        val minTs = System.currentTimeMillis() - TLL_1_DAYS

        val rows = dao(site).getFreshPage(queryKey, offset, minTs)

        if (rows.isEmpty()) return null
        return rows
            .asSequence()
            .filterNot(::isEmptyMarker)
            .map(mapper::toDomain)
            .toList()
    }

    override suspend fun getStalePageOrEmpty(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
    ): List<PostDomain> {
        val rows = dao(site).getPage(queryKey, offset)

        return rows
            .asSequence()
            .filterNot(::isEmptyMarker)
            .map(mapper::toDomain)
            .toList()
    }

    override suspend fun putPage(
        site: SelectedSite,
        queryKey: String,
        offset: Int,
        items: List<PostDomain>,
    ) {
        val now = System.currentTimeMillis()

        val entities = if (items.isEmpty()) {
            listOf(
                PostsSearchCacheEntity(
                    queryKey = queryKey,
                    offset = offset,
                    id = EMPTY_MARKER_ID,
                    userId = EMPTY_MARKER_USER_ID,
                    service = EMPTY_MARKER_SERVICE,
                    title = null,
                    substring = null,
                    published = null,
                    added = null,
                    edited = null,
                    incompleteRewardsJson = null,
                    pollJson = null,
                    fileName = null,
                    filePath = null,
                    attachmentsJson = "[]",
                    tagsJson = "[]",
                    nextId = null,
                    prevId = null,
                    indexInPage = 0,
                    updatedAt = now
                )
            )
        } else {
            items.mapIndexed { index, post ->
                mapper.toEntity(
                    post = post,
                    queryKey = queryKey,
                    offset = offset,
                    indexInPage = index,
                    updatedAt = now
                )
            }
        }

        dao(site).replacePage(queryKey, offset, entities)
    }

    private fun isEmptyMarker(entity: PostsSearchCacheEntity): Boolean =
        entity.id == EMPTY_MARKER_ID &&
                entity.userId == EMPTY_MARKER_USER_ID &&
                entity.service == EMPTY_MARKER_SERVICE

    override suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int) {
        dao(site).clearPage(queryKey, offset)
    }

    override suspend fun clearCache(site: SelectedSite) {
        val minTs = System.currentTimeMillis() - TLL_1_DAYS
        dao(site).deleteOlderThan(minTs)
    }

    override suspend fun clearAll(site: SelectedSite) {
        dao(site).clearAll()
    }
}
