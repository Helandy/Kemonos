package su.afk.kemonos.storage.api.repository.localLikes

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain

interface IStoreLocalLikedPostsRepository {
    suspend fun page(site: SelectedSite, limit: Int, offset: Int): List<PostDomain>
    suspend fun pageSearch(site: SelectedSite, query: String, limit: Int, offset: Int): List<PostDomain>

    suspend fun getAll(site: SelectedSite): List<PostDomain>

    suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean

    suspend fun add(site: SelectedSite, item: PostDomain)
    suspend fun remove(site: SelectedSite, service: String, creatorId: String, postId: String)
    suspend fun clear(site: SelectedSite)
}
