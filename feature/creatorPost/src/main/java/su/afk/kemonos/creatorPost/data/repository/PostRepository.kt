package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.data.api.PostsApi
import su.afk.kemonos.creatorPost.data.dto.profilePost.PostResponseDto.Companion.toDomain
import su.afk.kemonos.creatorPost.data.repository.helper.cacheFirstOrNetwork
import su.afk.kemonos.creatorPost.domain.repository.IPostRepository
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.network.util.call
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.repository.post.IStoragePostStorageRepository
import javax.inject.Inject

internal class PostRepository @Inject constructor(
    private val api: PostsApi,
    private val store: IStoragePostStorageRepository,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) : IPostRepository {

    /** Получение поста */
    override suspend fun getPost(service: String, id: String, postId: String): PostContentDomain {
        return cacheFirstOrNetwork(
            freshCache = { store.getFreshOrNull(service, id, postId) },
            network = {
                if (selectedSiteUseCase.getSite() == SelectedSite.P) {
                    api.getPawchiveProfilePost(service, id, postId).call { dto ->
                        dto.toDomain(
                            service = service,
                            creatorId = id,
                            postId = postId,
                        )
                    }
                } else {
                    api.getProfilePost(service, id, postId).call { dto ->
                        dto.toDomain()
                    }
                }
            },
            saveToCache = { fromNet -> store.upsert(fromNet) },
            staleCache = { store.getOrNull(service, id, postId) }
        )
    }

    override suspend fun getPostRevision(
        service: String,
        id: String,
        postId: String,
        revisionId: Long,
    ): PostContentDomain {
        val cachePostId = buildRevisionCachePostId(postId, revisionId)
        return cacheFirstOrNetwork(
            freshCache = { store.getFreshOrNull(service, id, cachePostId) },
            network = {
                if (selectedSiteUseCase.getSite() == SelectedSite.P) {
                    api.getPawchiveProfilePostRevision(service, id, postId, revisionId).call { dto ->
                        dto.toDomain(
                            service = service,
                            creatorId = id,
                            postId = postId,
                        )
                    }
                } else {
                    api.getProfilePostRevision(service, id, postId, revisionId).call { dto ->
                        dto.toDomain()
                    }
                }
            },
            saveToCache = { fromNet -> store.upsert(fromNet.withPostId(cachePostId)) },
            staleCache = { store.getOrNull(service, id, cachePostId) }
        )
    }

    private fun PostContentDomain.withPostId(postId: String): PostContentDomain = copy(
        post = post.copy(id = postId)
    )

    private fun buildRevisionCachePostId(postId: String, revisionId: Long): String {
        return "$REVISION_CACHE_PREFIX$postId:$revisionId"
    }

    private companion object {
        const val REVISION_CACHE_PREFIX = "__revision__:"
    }
}
