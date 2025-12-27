package su.afk.kemonos.core.preferences.site

import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.core.preferences.UrlPrefs
import su.afk.kemonos.domain.SelectedSite
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
 * Временное переключение сайта:
 *  - запоминаем старый
 *  - ставим нужный
 *  - выполняем блок
 *  - возвращаем старый обратно
 */
suspend inline fun <T> ISelectedSiteUseCase.withSite(
    site: SelectedSite,
    block: () -> T
): T {
    val previous = getSite()
    if (previous == site) return block()

    /** переключили */
    setSite(site)
    return try {
        block()
    } finally {
        setSite(previous)
    }
}