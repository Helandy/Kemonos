package su.afk.kemonos.posts.api.apiCheck

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem

data class ApiCheckForAllSitesResult(
    val checks: Map<SelectedSite, SingleSiteCheck>,
) {
    val allOk: Boolean get() = checks.values.all { it.success }

    fun error(site: SelectedSite): ErrorItem? = checks[site]?.error

    /** Ошибки только по тем источникам, где проверка не прошла. */
    val errors: Map<SelectedSite, ErrorItem>
        get() = checks.mapNotNull { (site, check) ->
            check.error?.let { site to it }
        }.toMap()
}

data class SingleSiteCheck(
    val site: SelectedSite,
    val success: Boolean,
    val error: ErrorItem? = null,
)
