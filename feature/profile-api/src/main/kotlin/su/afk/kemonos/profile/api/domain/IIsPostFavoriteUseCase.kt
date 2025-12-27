package su.afk.kemonos.profile.api.domain

import su.afk.kemonos.domain.SelectedSite

interface IIsPostFavoriteUseCase {
    suspend operator fun invoke(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean
}