package su.afk.kemonos.main.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.ClearAuthUseCase
import su.afk.kemonos.auth.IsAuthCoomerUseCase
import su.afk.kemonos.auth.IsAuthKemonoUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.network.util.isClientError4xx
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.withSite
import su.afk.kemonos.profile.api.domain.IGetAccountUseCase
import javax.inject.Inject

/**
 * Проверяет авторизацию по обоим сайтам:
 * - если по сайту нет сохранённой авторизации — вообще ничего не делаем
 * - если есть — дергаем getAccount() для этого сайта
 * - если прилетает 4xx — чистим сессию для этого сайта
 */
class CheckAuthForAllSitesUseCase @Inject constructor(
    private val selectedSiteProvider: ISelectedSiteUseCase,
    private val getAccountUseCase: IGetAccountUseCase,
    private val clearAuthUseCase: ClearAuthUseCase,
    private val isAuthKemonoUseCase: IsAuthKemonoUseCase,
    private val isAuthCoomerUseCase: IsAuthCoomerUseCase,
) {

    suspend operator fun invoke() = coroutineScope {
        /** 1. Есть ли авторизация по каждому сайту */
        val isKemonoAuth = isAuthKemonoUseCase().first()
        val isCoomerAuth = isAuthCoomerUseCase().first()

        /** 2. Параллельно дергаем сайты, по которым есть авторизация */
        val kemonoJob = if (isKemonoAuth) {
            async {
                val result = selectedSiteProvider.withSite(SelectedSite.K) {
                    getAccountUseCase()
                }

                if (result.isFailure) {
                    val throwable = result.exceptionOrNull()
                    if (throwable != null && throwable.isClientError4xx()) {
                        /** 4xx -> протухла сессия для Kemono */
                        clearAuthUseCase(SelectedSite.K)
                    }
                }
            }
        } else null

        val coomerJob = if (isCoomerAuth) {
            async {
                val result = selectedSiteProvider.withSite(SelectedSite.C) {
                    getAccountUseCase()
                }

                if (result.isFailure) {
                    val throwable = result.exceptionOrNull()
                    if (throwable != null && throwable.isClientError4xx()) {
                        /** 4xx -> протухла сессия для Coomer */
                        clearAuthUseCase(SelectedSite.C)
                    }
                }
            }
        } else null

        /** 3. Ждём */
        kemonoJob?.await()
        coomerJob?.await()
    }
}