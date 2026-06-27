package su.afk.kemonos.posts.api.apiCheck

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem

data class ApiCheckForAllSitesResult(
    val kemono: SingleSiteCheck,
    val coomer: SingleSiteCheck,
    val pawchive: SingleSiteCheck,
) {
    val allOk: Boolean get() = kemono.success && coomer.success && pawchive.success
}

data class SingleSiteCheck(
    val site: SelectedSite,
    val success: Boolean,
    val error: ErrorItem? = null,
)
