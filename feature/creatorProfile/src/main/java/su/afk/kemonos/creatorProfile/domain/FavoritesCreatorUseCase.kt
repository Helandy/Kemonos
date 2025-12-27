package su.afk.kemonos.creatorProfile.domain

import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.creatorProfile.data.IFavoritesCreatorRepository
import su.afk.kemonos.profile.api.domain.IIsCreatorFavoriteUseCase
import javax.inject.Inject

internal class FavoritesCreatorUseCase @Inject constructor(
    private val repository: IFavoritesCreatorRepository,
    private val isCreatorFavoriteUseCase: IIsCreatorFavoriteUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) {

    suspend fun addCreator(service: String, id: String) = runCatching {
        repository.addCreator(
            site = selectedSiteUseCase.getSite(),
            service = service,
            id = id,
        )
    }.getOrElse {
        false
    }

    suspend fun removeCreator(service: String, id: String) = runCatching {
        repository.removeCreator(
            site = selectedSiteUseCase.getSite(),
            service = service,
            id = id,
        )
    }.getOrElse {
        false
    }

    suspend fun isCreatorFavorite(service: String, id: String): Boolean {
        return isCreatorFavoriteUseCase.invoke(
            site = selectedSiteUseCase.getSite(),
            service = service,
            id = id,
        )
    }
}