package su.afk.kemonos.posts.domain.repository

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.posts.domain.model.dms.DmsPageDomain
import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.random.RandomDomain

internal interface IPostsRepository {
    suspend fun getPosts(
        site: SelectedSite,
        query: String?,
        tag: String?,
        offset: Int,
        forceRefresh: Boolean = false,
    ): List<PostDomain>

    suspend fun getDms(
        site: SelectedSite,
        offset: Int,
        limit: Int,
        query: String?,
        forceRefresh: Boolean = false,
    ): DmsPageDomain

    suspend fun getPopularPosts(
        site: SelectedSite,
        date: String?,
        period: Period,
        offset: Int,
        forceRefresh: Boolean = false,
    ): PopularPosts

    suspend fun getTags(site: SelectedSite, forceRefresh: Boolean = false): List<Tags>
    suspend fun getRandomPost(): RandomDomain
    suspend fun searchHash(hash: String): HashLookupDomain
}
