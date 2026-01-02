package su.afk.kemonos.posts.data

import su.afk.kemonos.api.domain.popular.PopularPosts
import su.afk.kemonos.api.domain.tags.Tags
import su.afk.kemonos.common.data.common.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.network.util.call
import su.afk.kemonos.posts.data.api.PostsApi
import su.afk.kemonos.posts.data.dto.popular.response.PopularPostsDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.random.RandomDto.Companion.toDomain
import su.afk.kemonos.posts.data.dto.tags.TagsDto.Companion.toDomain
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.popular.Period.Companion.toDto
import su.afk.kemonos.posts.domain.model.random.RandomDomain
import su.afk.kemonos.posts.util.Utils.queryKey
import su.afk.kemonos.storage.api.popular.IPopularPostsCacheUseCase
import su.afk.kemonos.storage.api.postsSearch.IPostsSearchCacheUseCase
import su.afk.kemonos.storage.api.tags.IStoreTagsUseCase
import javax.inject.Inject

internal interface IPostsRepository {
    suspend fun getPosts(site: SelectedSite, query: String?, tag: String?, offset: Int): List<PostDomain>
    suspend fun getPopularPosts(site: SelectedSite, date: String?, period: Period, offset: Int): PopularPosts

    suspend fun getTags(site: SelectedSite): List<Tags>
    suspend fun getRandomPost(): RandomDomain
}

internal class PostsRepository @Inject constructor(
    private val postsApi: PostsApi,
    private val tagsStore: IStoreTagsUseCase,
    private val postsSearchCache: IPostsSearchCacheUseCase,
    private val popularCache: IPopularPostsCacheUseCase,
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
}