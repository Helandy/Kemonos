package su.afk.kemonos.creatorProfile.presenter.delegates

import kotlinx.coroutines.flow.first
import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.core.auth.IsAuthCoomerUseCase
import su.afk.kemonos.core.auth.IsAuthKemonoUseCase
import su.afk.kemonos.creatorProfile.domain.FavoritesCreatorUseCase
import su.afk.kemonos.domain.SelectedSite
import javax.inject.Inject

internal class LikeDelegate @Inject constructor(
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val isAuthKemonoUseCase: IsAuthKemonoUseCase,
    private val isAuthCoomerUseCase: IsAuthCoomerUseCase,
    private val favoritesCreatorUseCase: FavoritesCreatorUseCase,
) {

    /** Проверка можно ли лайкнуть */
    suspend fun creatorIsAvailableLike(): Boolean {
        return when (selectedSiteUseCase.getSite()) {
            SelectedSite.C -> isAuthCoomerUseCase().first()
            SelectedSite.K -> isAuthKemonoUseCase().first()
        }
    }

    /** добавить в избранное */
    /** удалить из избранное */
    suspend fun onFavoriteClick(isFavorite: Boolean, service: String, id: String): Boolean {
        // todo сделать получение ошибок в тост бар
        return if (isFavorite) {
            favoritesCreatorUseCase.removeCreator(service = service, id = id)
        } else {
            favoritesCreatorUseCase.addCreator(service = service, id = id)
        }
    }

    /** проверит в избранном ли автор */
    suspend fun isCreatorFavorite(service: String, id: String): Boolean {
        return favoritesCreatorUseCase.isCreatorFavorite(
            service = service,
            id = id
        )
    }
}