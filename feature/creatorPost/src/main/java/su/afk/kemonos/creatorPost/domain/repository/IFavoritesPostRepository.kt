package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain

internal interface IFavoritesPostRepository {
    suspend fun addPost(site: SelectedSite, post: PostDomain): Result<Unit>
    suspend fun removePost(
        site: SelectedSite,
        service: String,
        creatorId: String,
        postId: String
    ): Result<Unit>
}
