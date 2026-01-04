package su.afk.kemonos.preferences.site

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.UrlPrefs
import javax.inject.Inject

internal class SelectedSiteUseCase @Inject constructor(
    private val prefs: UrlPrefs,
) : ISelectedSiteUseCase {
    override val selectedSite: StateFlow<SelectedSite> = prefs.selectedSite

    override suspend fun setSite(site: SelectedSite) {
        prefs.setSelectedSite(site)
    }

    override fun getSite(): SelectedSite = selectedSite.value
}

/**
 * Один глобальный замок на временное переключение.
 * Иначе параллельные withSite(K) и withSite(C) будут гонять DataStore и ломать baseUrl.
 */
val SITE_SWITCH_MUTEX = Mutex()

/**
 * Временное переключение сайта:
 *  - запоминаем старый
 *  - ставим нужный
 *  - выполняем блок
 *  - возвращаем старый обратно
 *
 * ВАЖНО: block теперь suspend, чтобы можно было вызывать retrofit suspend-функции внутри.
 */
suspend inline fun <T> ISelectedSiteUseCase.withSite(
    site: SelectedSite,
    crossinline block: suspend () -> T
): T = SITE_SWITCH_MUTEX.withLock {
    val previous = getSite()
    if (previous == site) return@withLock block()

    setSite(site)
    try {
        block()
    } finally {
        setSite(previous)
    }
}