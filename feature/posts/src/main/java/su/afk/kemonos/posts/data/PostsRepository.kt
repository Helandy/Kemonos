package su.afk.kemonos.posts.data

import su.afk.kemonos.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.network.util.call
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.posts.data.api.PostsApi
import su.afk.kemonos.posts.data.dto.dms.toDomain
import su.afk.kemonos.posts.data.dto.hashLookup.toDomain
import su.afk.kemonos.posts.data.dto.popular.response.PopularPostsDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.random.RandomDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.tags.TagsDto.Companion.toDomain
import su.afk.kemonos.posts.domain.model.dms.DmDomain
import su.afk.kemonos.posts.domain.model.dms.DmsPageDomain
import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.popular.Period.Companion.toDto
import su.afk.kemonos.posts.domain.model.random.RandomDomain
import su.afk.kemonos.posts.util.Utils.queryKey
import su.afk.kemonos.storage.api.repository.dms.IStorageDmsRepository
import su.afk.kemonos.storage.api.repository.dms.StorageDmItem
import su.afk.kemonos.storage.api.repository.popular.IStoragePopularPostsRepository
import su.afk.kemonos.storage.api.repository.postsSearch.IStoragePostsSearchRepository
import su.afk.kemonos.storage.api.repository.tags.IStoreTagsRepository
import javax.inject.Inject

internal interface IPostsRepository {
    suspend fun getPosts(site: SelectedSite, query: String?, tag: String?, offset: Int): List<PostDomain>
    suspend fun getDms(site: SelectedSite, offset: Int, limit: Int, query: String?): DmsPageDomain
    suspend fun getPopularPosts(site: SelectedSite, date: String?, period: Period, offset: Int): PopularPosts

    suspend fun getTags(site: SelectedSite): List<Tags>
    suspend fun getRandomPost(): RandomDomain
    suspend fun searchHash(hash: String): HashLookupDomain
}

internal class PostsRepository @Inject constructor(
    private val postsApi: PostsApi,
    private val tagsStore: IStoreTagsRepository,
    private val postsSearchCache: IStoragePostsSearchRepository,
    private val dmsCache: IStorageDmsRepository,
    private val popularCache: IStoragePopularPostsRepository,
) : IPostsRepository{

    /** Поиск постов */
    override suspend fun getPosts(
        site: SelectedSite,
        query: String?,
        tag: String?,
        offset: Int,
    ): List<PostDomain> {
        val qk = queryKey(query, tag)

        postsSearchCache.getFreshPageOrNull(site, qk, offset)
            ?.let { return it }

        val apiOffset = if (offset == 0) null else offset

        val net = postsApi.getPosts(
            search = query,
            offset = apiOffset,
            tag = tag,
        ).call { dto ->
            dto.posts.orEmpty().map { it.toDomain() }
        }

        if (net.isNotEmpty()) {
            postsSearchCache.putPage(site, qk, offset, net)
        }

        return net
    }

    override suspend fun getDms(
        site: SelectedSite,
        offset: Int,
        limit: Int,
        query: String?,
    ): DmsPageDomain {
        val normalizedQuery = query?.trim()?.ifEmpty { null }
        val qk = normalizedQuery.orEmpty()
        dmsCache.getFreshPageOrNull(site = site, queryKey = qk, offset = offset)
            ?.let { cached ->
                return DmsPageDomain(
                    count = cached.size,
                    limit = limit,
                    dms = cached.map { it.toDomain() },
                )
            }

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
                items = net.dms.map { it.toStorageItem() },
            )
        } else {
            dmsCache.clearPage(site = site, queryKey = qk, offset = offset)
        }

        return net
    }

    /** Популярные посты */
    override suspend fun getPopularPosts(site: SelectedSite, date: String?, period: Period, offset: Int): PopularPosts {
        val qk = "${period.name}|${date.orEmpty()}"

        popularCache.getFreshOrNull(site, qk, offset)
            ?.let { return it }

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
        }

        return net
    }

    /** Все теги */
    override suspend fun getTags(site: SelectedSite): List<Tags> {
        if (tagsStore.isCacheFresh(site)) {
            return tagsStore.getAll(site)
        }

        val net = postsApi.getTags().call { response ->
            response.map { it.toDomain() }
        }

        if (net.isNotEmpty()) {
            tagsStore.update(site, net)
        }

        return net
    }

    /** Рандомный пост */
    override suspend fun getRandomPost(): RandomDomain = postsApi.getRandomPost()
        .call { dto -> dto.toDomain() }

    override suspend fun searchHash(hash: String): HashLookupDomain = postsApi.searchHash(hash)
        .call { dto -> dto.toDomain() }
}

private fun StorageDmItem.toDomain(): DmDomain = DmDomain(
    added = added,
    content = content,
    hash = hash,
    published = published,
    service = service,
    user = user,
    artistId = artistId,
    artistName = artistName,
    artistUpdated = artistUpdated,
)

private fun DmDomain.toStorageItem(): StorageDmItem = StorageDmItem(
    hash = hash,
    service = service,
    user = user,
    content = content,
    added = added,
    published = published,
    artistId = artistId,
    artistName = artistName,
    artistUpdated = artistUpdated,
)
