package su.afk.kemonos.auth.domain.model

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AuthUser

data class SiteAuthState(
    val session: String?,
    val user: AuthUser?,
) {
    val isAuthorized: Boolean
        get() = session != null && user != null

    companion object {
        val EMPTY = SiteAuthState(null, null)
    }
}

data class AuthState(
    val sites: Map<SelectedSite, SiteAuthState> = emptyMap(),
) {
    fun forSite(site: SelectedSite): SiteAuthState = sites[site] ?: SiteAuthState.EMPTY

    fun isAuthorized(site: SelectedSite): Boolean = forSite(site).isAuthorized
}
