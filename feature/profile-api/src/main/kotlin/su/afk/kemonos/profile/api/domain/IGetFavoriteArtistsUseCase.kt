package su.afk.kemonos.profile.api.domain

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.FavoriteArtist

interface IGetFavoriteArtistsUseCase {
    suspend operator fun invoke(site: SelectedSite, checkDifferent: Boolean = false): List<FavoriteArtist>
}