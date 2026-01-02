package su.afk.kemonos.storage.api.popular

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.popular.PopularPosts

interface IPopularPostsCacheUseCase {
    suspend fun getFreshOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts?
    suspend fun getStaleOrNull(site: SelectedSite, queryKey: String, offset: Int): PopularPosts?
    suspend fun put(site: SelectedSite, queryKey: String, offset: Int, value: PopularPosts)
    suspend fun clearCache(site: SelectedSite)
}