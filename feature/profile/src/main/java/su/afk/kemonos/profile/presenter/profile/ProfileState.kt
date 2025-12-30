package su.afk.kemonos.profile.presenter.profile

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.Login

internal data class ProfileState(
    val isLoading: Boolean = true,

    val isLoginKemono: Boolean = false,
    val isLoginCoomer: Boolean = false,
    val isLogin: Boolean = false,

    val kemonoLogin: Login? = null,
    val coomerLogin: Login? = null,

    val kemonoUrl: String = "",
    val coomerUrl: String = "",
    val appVersion: String = "",

    val showLogoutConfirm: Boolean = false,
    val logoutSite: SelectedSite? = null,
)