package su.afk.kemonos.main.domain

import su.afk.kemonos.auth.IsAuthSiteUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.ClearAuthUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.network.util.isClientError4xx
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import javax.inject.Inject

/**
 * Проверяет авторизацию по обоим сайтам:
 * - если по сайту нет сохранённой авторизации — pass
 * - если есть — дергаем getFavoriteArtistsUseCase() для этого сайта
 * - если прилетает 4xx — чистим сессию для этого сайта
 */
class CheckAuthForAllSitesUseCase @Inject constructor(
    private val getFavoriteArtistsUseCase: IGetFavoriteArtistsUseCase,
    private val clearAuthUseCase: ClearAuthUseCase,
    private val isAuthSiteUseCase: IsAuthSiteUseCase,
) {

    suspend operator fun invoke(enabledSites: Set<SelectedSite>): Set<SelectedSite> = coroutineScope {
        val needApiCheck = mutableSetOf<SelectedSite>()

        enabledSites.forEach { site ->
            /** Нет сессии — или она не пережила проверку: сайт идёт в обычную проверку API. */
            val hasSession = isAuthSiteUseCase(site).first()
            if (!hasSession || !checkSiteAuth(site)) {
                needApiCheck += site
            }
        }

        needApiCheck
    }

    /**
     * @return true если авторизация валидна (или не доказано обратное),
     *         false если словили 4xx и почистили сессию
     */
    private suspend fun checkSiteAuth(site: SelectedSite): Boolean {
        /** Репозиторий сам ходит на нужный источник — временное переключение не требуется. */
        val result = runCatching {
            getFavoriteArtistsUseCase(site = site, checkDifferent = true)
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
