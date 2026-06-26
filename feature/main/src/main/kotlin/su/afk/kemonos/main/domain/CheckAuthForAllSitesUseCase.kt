package su.afk.kemonos.main.domain

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.ClearAuthUseCase
import su.afk.kemonos.auth.IsAuthCoomerUseCase
import su.afk.kemonos.auth.IsAuthKemonoUseCase
import su.afk.kemonos.auth.IsAuthPawchiveUseCase
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
    private val isAuthPawchiveUseCase: IsAuthPawchiveUseCase,
) {

    suspend operator fun invoke(enabledSites: Set<SelectedSite>): Set<SelectedSite> = coroutineScope {
        val needApiCheck = mutableSetOf<SelectedSite>()

        val isCoomerAuth = isAuthCoomerUseCase().first()
        val isKemonoAuth = isAuthKemonoUseCase().first()
        val isPawchiveAuth = isAuthPawchiveUseCase().first()

        if (SelectedSite.C in enabledSites && isCoomerAuth) {
            val stillAuth = checkSiteAuth(SelectedSite.C)
            if (!stillAuth) needApiCheck += SelectedSite.C
        } else if (SelectedSite.C in enabledSites) {
            needApiCheck += SelectedSite.C
        }

        if (SelectedSite.K in enabledSites && isKemonoAuth) {
            val stillAuth = checkSiteAuth(SelectedSite.K)
            if (!stillAuth) needApiCheck += SelectedSite.K
        } else if (SelectedSite.K in enabledSites) {
            needApiCheck += SelectedSite.K
        }

        if (SelectedSite.P in enabledSites && isPawchiveAuth) {
            val stillAuth = checkSiteAuth(SelectedSite.P)
            if (!stillAuth) needApiCheck += SelectedSite.P
        } else if (SelectedSite.P in enabledSites) {
            needApiCheck += SelectedSite.P
        }

        needApiCheck
    }

    /**
     * @return true если авторизация валидна (или не доказано обратное),
     *         false если словили 4xx и почистили сессию
     */
    private suspend fun checkSiteAuth(site: SelectedSite): Boolean {
        val result = selectedSiteProvider.withSite(site) {
            runCatching {
                getFavoriteArtistsUseCase(site = site, checkDifferent = true)
            }
        }

        if (result.isFailure) {
            val throwable = result.exceptionOrNull()
            if (throwable != null && throwable.isClientError4xx()) {
                clearAuthUseCase(site)
                return false
            }
        }

        return true
    }
}
