package su.afk.kemonos.preferences.site

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
 * Насовсем переключает источник и ждёт, пока [selectedSite] отдаст новое значение.
 *
 * Нужно там, где переход по ссылке ведёт на контент другого источника: дальше
 * экран уже работает как обычно, с «текущим» сайтом.
 *
 * Для разового обращения к другому источнику это не подходит — берите
 * SiteRetrofitProvider, который не трогает глобальное состояние.
 */
suspend fun ISelectedSiteUseCase.setSiteAndAwait(site: SelectedSite) {
    setSite(site)
    selectedSite.first { it == site }
}

/*
 * Здесь же раньше жили SITE_SWITCH_MUTEX и withSite: чтобы сходить на другой источник,
 * выбранный сайт временно подменялся и возвращался обратно.
 *
 * Приём был неисправен по двум причинам:
 *  - подмена доезжала до сетевого клиента асинхронно, и запрос успевал уйти на прежний хост;
 *  - подмена видна всем, поэтому параллельный запрос «текущего» сайта уходил на чужой.
 */
