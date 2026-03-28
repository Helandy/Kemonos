package su.afk.kemonos.auth.domain.model

import su.afk.kemonos.domain.models.AuthUser

data class SiteAuthState(
    val session: String?,
    val user: AuthUser?,
) {
    val isAuthorized: Boolean
        get() = session != null && user != null
}

data class AuthState(
    val kemono: SiteAuthState = SiteAuthState(null, null),
    val coomer: SiteAuthState = SiteAuthState(null, null),
) {
    val isKemonoAuthorized: Boolean get() = kemono.isAuthorized
    val isCoomerAuthorized: Boolean get() = coomer.isAuthorized
}
