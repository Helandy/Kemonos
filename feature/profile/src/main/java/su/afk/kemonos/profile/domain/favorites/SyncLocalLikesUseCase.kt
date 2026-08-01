package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.domain.repository.IFavoritesRepository
import javax.inject.Inject

internal class SyncLocalLikesUseCase @Inject constructor(
    private val repository: IFavoritesRepository
) {
    suspend operator fun invoke(site: SelectedSite) {
        repository.syncLocalLikes(site)
    }
}
