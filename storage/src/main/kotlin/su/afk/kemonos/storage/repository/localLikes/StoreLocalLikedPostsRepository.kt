package su.afk.kemonos.storage.repository.localLikes

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.storage.api.repository.localLikes.IStoreLocalLikedPostsRepository
import su.afk.kemonos.storage.entity.localLikes.LocalLikedPostsDao
import su.afk.kemonos.storage.entity.localLikes.mapper.LocalLikedPostMapper
import javax.inject.Inject

internal class StoreLocalLikedPostsRepository @Inject constructor(
    private val dao: LocalLikedPostsDao,
    private val mapper: LocalLikedPostMapper,
) : IStoreLocalLikedPostsRepository {

    override suspend fun page(site: SelectedSite, limit: Int, offset: Int): List<PostDomain> =
        dao.page(site = site, limit = limit, offset = offset).map(mapper::toDomain)

    override suspend fun pageSearch(site: SelectedSite, query: String, limit: Int, offset: Int): List<PostDomain> =
        dao.pageSearch(site = site, query = query, limit = limit, offset = offset).map(mapper::toDomain)

    override suspend fun getAll(site: SelectedSite): List<PostDomain> =
        dao.getAll(site).map(mapper::toDomain)

    override suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean =
        dao.exists(site, service, creatorId, postId)

    override suspend fun add(site: SelectedSite, item: PostDomain) {
        dao.upsert(mapper.toEntity(site, item))
    }

    override suspend fun remove(site: SelectedSite, service: String, creatorId: String, postId: String) {
        dao.delete(site, service, creatorId, postId)
    }

    override suspend fun clear(site: SelectedSite) {
        dao.clear(site)
    }
}
