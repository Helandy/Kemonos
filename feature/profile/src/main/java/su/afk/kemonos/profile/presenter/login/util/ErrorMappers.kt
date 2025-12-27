package su.afk.kemonos.profile.presenter.login.util

import androidx.annotation.StringRes
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.domain.login.LoginPasswordErrorCode
import su.afk.kemonos.profile.domain.login.LoginUsernameErrorCode

@StringRes
internal fun loginUsernameErrorRes(code: LoginUsernameErrorCode): Int = when (code) {
    LoginUsernameErrorCode.EMPTY -> R.string.login_username_empty
}

@StringRes
internal fun loginPasswordErrorRes(code: LoginPasswordErrorCode): Int = when (code) {
    LoginPasswordErrorCode.EMPTY -> R.string.login_password_empty
}
