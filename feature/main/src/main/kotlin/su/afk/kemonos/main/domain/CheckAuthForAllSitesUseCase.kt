package su.afk.kemonos.main.domain

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.ClearAuthUseCase
import su.afk.kemonos.auth.IsAuthCoomerUseCase
import su.afk.kemonos.auth.IsAuthKemonoUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.network.util.isClientError4xx
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.withSite
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import javax.inject.Inject

/**
 * Проверяет авторизацию по обоим сайтам:
 * - если по сайту нет сохранённой авторизации — pass
 * - если есть — дергаем getFavoriteArtistsUseCase() для этого сайта
 * - если прилетает 4xx — чистим сессию для этого сайта
 */
class CheckAuthForAllSitesUseCase @Inject constructor(
    private val selectedSiteProvider: ISelectedSiteUseCase,
    private val getFavoriteArtistsUseCase: IGetFavoriteArtistsUseCase,
    private val clearAuthUseCase: ClearAuthUseCase,
    private val isAuthKemonoUseCase: IsAuthKemonoUseCase,
    private val isAuthCoomerUseCase: IsAuthCoomerUseCase,
) {

    suspend operator fun invoke() = coroutineScope {
        val isKemonoAuth = isAuthKemonoUseCase().first()
        val isCoomerAuth = isAuthCoomerUseCase().first()

        if (isKemonoAuth) checkSiteAuth(SelectedSite.K)
        if (isCoomerAuth) checkSiteAuth(SelectedSite.C)
    }

    private suspend fun checkSiteAuth(site: SelectedSite) {
        val result = selectedSiteProvider.withSite(site) {
            runCatching {
                /**
                 * 1) проверить сессию,
                 * 2) обновить кэш избранного
                 * */
                getFavoriteArtistsUseCase(site = site, checkDifferent = true)
            }
        }

        if (result.isFailure) {
            val throwable = result.exceptionOrNull()
            if (throwable != null && throwable.isClientError4xx()) {
                clearAuthUseCase(site)
            }
        }
    }
}