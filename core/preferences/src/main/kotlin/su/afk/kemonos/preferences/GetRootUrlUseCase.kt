package su.afk.kemonos.preferences

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.utils.url.toRootUrl
import javax.inject.Inject
import javax.inject.Singleton

/** Корневой адрес источника: "https://kemono.cr/api/" -> "https://kemono.cr". */
@Singleton
class GetRootUrlUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) {
    operator fun invoke(site: SelectedSite): String =
        urlPrefs.siteUrl(site).value.toRootUrl()
}
