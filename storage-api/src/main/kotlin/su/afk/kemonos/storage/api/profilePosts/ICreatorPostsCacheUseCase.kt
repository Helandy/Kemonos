package su.afk.kemonos.storage.api.profilePosts

import su.afk.kemonos.domain.models.PostDomain

interface ICreatorPostsCacheUseCase {
    suspend fun getFreshPageOrNull(queryKey: String, offset: Int): List<PostDomain>?
    suspend fun getStalePageOrEmpty(queryKey: String, offset: Int): List<PostDomain>
    suspend fun putPage(queryKey: String, offset: Int, items: List<PostDomain>)
    suspend fun clearPage(queryKey: String, offset: Int)
}