package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.profile.data.IFavoritesRepository
import javax.inject.Inject

internal class GetFavoritePostsUseCase @Inject constructor(
    private val repository: IFavoritesRepository
) {
    suspend operator fun invoke(site: SelectedSite): List<PostDomain> {
        return repository.getFavoritePosts(site = site)
    }
}