package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.data.api.PostsApi
import su.afk.kemonos.creatorPost.data.dto.profilePost.PostResponseDto.Companion.toDomain
import su.afk.kemonos.creatorPost.data.repository.helper.cacheFirstOrNetwork
import su.afk.kemonos.creatorPost.domain.repository.IPostRepository
import su.afk.kemonos.network.util.call
import su.afk.kemonos.storage.api.repository.post.IStoragePostStorageRepository
import javax.inject.Inject

internal class PostRepository @Inject constructor(
    private val api: PostsApi,
    private val store: IStoragePostStorageRepository
) : IPostRepository {

    /** Получение поста */
    override suspend fun getPost(service: String, id: String, postId: String): PostContentDomain {
        return cacheFirstOrNetwork(
            freshCache = { store.getFreshOrNull(service, id, postId) },
            network = {
                api.getProfilePost(service, id, postId).call { dto ->
                    dto.toDomain()
                }
            },
            saveToCache = { fromNet -> store.upsert(fromNet) },
            staleCache = { store.getOrNull(service, id, postId) }
        )
    }
}
