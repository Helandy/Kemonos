package su.afk.kemonos.posts.data

import kotlinx.coroutines.CancellationException
import su.afk.kemonos.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.network.util.call
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.posts.api.tags.Tags.Companion.normalizeTags
import su.afk.kemonos.posts.data.api.PostsApi
import su.afk.kemonos.posts.data.dto.dms.toDomain
import su.afk.kemonos.posts.data.dto.hashLookup.toDomain
import su.afk.kemonos.posts.data.dto.popular.request.toDto
import su.afk.kemonos.posts.data.dto.popular.response.PopularPostsDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.random.RandomDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.tags.TagsDto.Companion.toDomain
import su.afk.kemonos.posts.domain.model.dms.DmsPageDomain
import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.random.RandomDomain
import su.afk.kemonos.posts.domain.repository.IPostsRepository
import su.afk.kemonos.storage.api.repository.dms.IStorageDmsRepository
import su.afk.kemonos.storage.api.repository.popular.IStoragePopularPostsRepository
import su.afk.kemonos.storage.api.repository.postsSearch.IStoragePostsSearchRepository
import su.afk.kemonos.storage.api.repository.tags.IStoreTagsRepository
import su.afk.kemonos.utils.posts.buildPostsQueryKey
import javax.inject.Inject

internal class PostsRepository @Inject constructor(
    private val postsApi: PostsApi,
    private val tagsStore: IStoreTagsRepository,
    private val postsSearchCache: IStoragePostsSearchRepository,
    private val dmsCache: IStorageDmsRepository,
    private val popularCache: IStoragePopularPostsRepository,
) : IPostsRepository {

    /** Поиск постов */
    override suspend fun getPosts(
        site: SelectedSite,
        query: String?,
        tag: String?,
        offset: Int,
        forceRefresh: Boolean,
    ): List<PostDomain> {
        val normalizedQuery = query?.trim()?.ifEmpty { null }
        val normalizedTag = tag?.trim()?.ifEmpty { null }
        val qk = buildPostsQueryKey(normalizedQuery, normalizedTag)

        if (!forceRefresh) {
            postsSearchCache.getFreshPageOrNull(site, qk, offset)
                ?.let { return it }
        }

        return try {
            val apiOffset = if (offset == 0) null else offset

            val net = postsApi.getPosts(
                search = normalizedQuery,
                offset = apiOffset,
                tag = normalizedTag,
            ).call { dto ->
                dto.posts.orEmpty().map { it.toDomain() }
            }

            if (net.isNotEmpty()) {
                postsSearchCache.putPage(site, qk, offset, net)
            } else {
                postsSearchCache.clearPage(site, qk, offset)
            }

            net
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (forceRefresh) throw e
            val stale = postsSearchCache.getStalePageOrEmpty(site, qk, offset)
            if (stale.isNotEmpty()) stale else throw e
        }
    }

    override suspend fun getDms(
        site: SelectedSite,
        offset: Int,
        limit: Int,
        query: String?,
        forceRefresh: Boolean,
    ): DmsPageDomain {
        val normalizedQuery = query?.trim()?.ifEmpty { null }
        val qk = normalizedQuery.orEmpty()
        if (!forceRefresh) {
            dmsCache.getFreshPageOrNull(site = site, queryKey = qk, offset = offset)
                ?.let { cached ->
                    return DmsPageDomain(
                        count = DmsPageDomain.UNKNOWN_COUNT,
                        limit = limit,
                        dms = cached,
                    )
                }
        }

        return try {
            val apiOffset = if (offset == 0) null else offset

            val net = postsApi.getDms(
                offset = apiOffset,
                limit = limit,
                query = normalizedQuery,
            ).call { dto ->
                dto.toDomain(requestedLimit = limit)
            }

            if (net.dms.isNotEmpty()) {
                dmsCache.putPage(
                    site = site,
                    queryKey = qk,
                    offset = offset,
                    items = net.dms,
                )
            } else {
                dmsCache.clearPage(site = site, queryKey = qk, offset = offset)
            }

            net
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (forceRefresh) throw e
            val stale = dmsCache.getStalePageOrEmpty(site = site, queryKey = qk, offset = offset)
            if (stale.isNotEmpty()) {
                DmsPageDomain(
                    count = DmsPageDomain.UNKNOWN_COUNT,
                    limit = limit,
                    dms = stale,
                )
            } else {
                throw e
            }
        }
    }

    /** Популярные посты */
    override suspend fun getPopularPosts(
        site: SelectedSite,
        date: String?,
        period: Period,
        offset: Int,
        forceRefresh: Boolean,
    ): PopularPosts {
        val qk = "${period.name}|${date.orEmpty()}"

        if (!forceRefresh) {
            popularCache.getFreshOrNull(site, qk, offset)
                ?.let { return it }
        }

        return try {
            val apiOffset = if (offset == 0) null else offset

            val net = postsApi.getPopularPosts(
                date = date,
                period = period.toDto(),
                offset = apiOffset,
            ).call { dto ->
                dto.toDomain()
            }

            if (net.posts.isNotEmpty()) {
                popularCache.put(site, qk, offset, net)
            } else {
                popularCache.clearPage(site, qk, offset)
            }

            net
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (forceRefresh) throw e
            popularCache.getStaleOrNull(site, qk, offset) ?: throw e
        }
    }

    /** Все теги */
    override suspend fun getTags(site: SelectedSite, forceRefresh: Boolean): List<Tags> {
        val cached = tagsStore.getAll(site).normalizeTags()
        if (!forceRefresh && cached.isNotEmpty() && tagsStore.isCacheFresh(site)) {
            return cached
        }

        return try {
            val net = postsApi.getTags().call { response ->
                response.map { it.toDomain() }
            }.normalizeTags()

            if (net.isNotEmpty()) {
                tagsStore.update(site, net)
                net
            } else {
                if (forceRefresh) net else cached
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (forceRefresh) throw e
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    /** Рандомный пост */
    override suspend fun getRandomPost(): RandomDomain = postsApi.getRandomPost()
        .call { dto -> dto.toDomain() }

    override suspend fun searchHash(hash: String): HashLookupDomain = postsApi.searchHash(hash)
        .call { dto -> dto.toDomain() }
}
