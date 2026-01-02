package su.afk.kemonos.auth

import su.afk.kemonos.profile.api.model.Login

data class SiteAuthState(
    val session: String?,
    val user: Login?,
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
