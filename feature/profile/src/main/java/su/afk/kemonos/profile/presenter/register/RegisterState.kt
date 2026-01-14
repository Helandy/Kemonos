package su.afk.kemonos.profile.presenter.register

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.profile.domain.register.ConfirmErrorCode
import su.afk.kemonos.profile.domain.register.PasswordErrorCode
import su.afk.kemonos.profile.domain.register.UsernameErrorCode

internal data class RegisterState(
    val selectSite: SelectedSite = SelectedSite.K,

    val isLoading: Boolean = false,
    val error: ErrorItem? = null,

    val username: String = "",
    val password: String = "",
    val confirm: String = "",

    val usernameError: UsernameErrorCode? = null,
    val passwordError: PasswordErrorCode? = null,
    val confirmError: ConfirmErrorCode? = null,
)

sealed interface RegisterEffect {
    data class SavePassword(val username: String, val password: String) : RegisterEffect
}