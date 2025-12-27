package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.core.api.domain.net.helpers.call
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.data.api.PostsApi
import su.afk.kemonos.creatorPost.data.dto.profilePost.PostResponseDto.Companion.toDomain
import su.afk.kemonos.storage.api.post.IPostUseCase
import javax.inject.Inject

internal interface IPostRepository {
    suspend fun getPost(service: String, id: String, postId: String): PostContentDomain?
}

internal class PostRepository @Inject constructor(
    private val api: PostsApi,
    private val postUseCase: IPostUseCase
) : IPostRepository {

    /** Получение поста */
    override suspend fun getPost(service: String, id: String, postId: String): PostContentDomain {
        postUseCase.getFreshOrNull(service, id, postId)?.let { return it }

        val fromNet = api.getProfilePost(service, id, postId).call { dto ->
            dto.toDomain()
        }

        postUseCase.upsert(fromNet)
        return fromNet
    }
}