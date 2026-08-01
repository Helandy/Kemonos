package su.afk.kemonos.creatorProfile.presenter.creatorProfile.delegates

import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.IsAuthCoomerUseCase
import su.afk.kemonos.auth.IsAuthKemonoUseCase
import su.afk.kemonos.auth.IsAuthPawchiveUseCase
import su.afk.kemonos.creatorProfile.domain.FavoritesCreatorUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.repository.localLikes.IStoreLocalLikedArtistsRepository
import javax.inject.Inject

internal class LikeDelegate @Inject constructor(
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val isAuthKemonoUseCase: IsAuthKemonoUseCase,
    private val isAuthCoomerUseCase: IsAuthCoomerUseCase,
    private val isAuthPawchiveUseCase: IsAuthPawchiveUseCase,
    private val favoritesCreatorUseCase: FavoritesCreatorUseCase,
    private val localLikedArtistsRepository: IStoreLocalLikedArtistsRepository,
) {

    /** Проверка авторизован ли пользователь на выбранном сайте (лайк без авторизации хранится только локально) */
    suspend fun creatorIsAvailableLike(): Boolean {
        return when (selectedSiteUseCase.getSite()) {
            SelectedSite.C -> isAuthCoomerUseCase().first()
            SelectedSite.K -> isAuthKemonoUseCase().first()
            SelectedSite.P -> isAuthPawchiveUseCase().first()
        }
    }

    /** добавить/удалить лайк автора: авторизован — через API, иначе — только локально */
    suspend fun onFavoriteClick(isFavorite: Boolean, profile: Profile?, service: String, id: String): Result<Unit> {
        val site = selectedSiteUseCase.getSite()

        if (!creatorIsAvailableLike()) {
            return runCatching {
                if (isFavorite) {
                    localLikedArtistsRepository.remove(site = site, service = service, id = id)
                } else {
                    val domain = profile?.toFavoriteArtist() ?: throw IllegalStateException("Profile is null")
                    localLikedArtistsRepository.add(site = site, item = domain)
                }
            }
        }

        return if (isFavorite) favoritesCreatorUseCase.removeCreator(service, id)
        else favoritesCreatorUseCase.addCreator(service, id)
    }

    /** проверит в избранном/локальных лайках ли автор */
    suspend fun isCreatorFavorite(service: String, id: String): Boolean {
        val site = selectedSiteUseCase.getSite()

        if (!creatorIsAvailableLike()) {
            return runCatching {
                localLikedArtistsRepository.exists(site = site, service = service, creatorId = id)
            }.getOrElse { false }
        }

        return runCatching {
            favoritesCreatorUseCase.isCreatorFavorite(
                service = service,
                id = id
            )
        }.getOrElse { false }
    }

    private fun Profile.toFavoriteArtist(): FavoriteArtist = FavoriteArtist(
        favedSeq = 0,
        id = id,
        indexed = indexed.orEmpty(),
        lastImported = "",
        name = name,
        publicId = publicId.orEmpty(),
        relationId = relationId ?: 0,
        service = service,
        updated = updated.orEmpty(),
    )
}
