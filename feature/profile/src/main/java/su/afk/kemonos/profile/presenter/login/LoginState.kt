package su.afk.kemonos.profile.presenter.login

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.domain.models.ErrorItem
import su.afk.kemonos.profile.domain.login.LoginPasswordErrorCode
import su.afk.kemonos.profile.domain.login.LoginUsernameErrorCode

internal data class LoginState(
    val selectSite: SelectedSite = SelectedSite.K,

    val isLoading: Boolean = false,
    val error: ErrorItem? = null,

    val username: String = "",
    val password: String = "",

    val usernameError: LoginUsernameErrorCode? = null,
    val passwordError: LoginPasswordErrorCode? = null,
)