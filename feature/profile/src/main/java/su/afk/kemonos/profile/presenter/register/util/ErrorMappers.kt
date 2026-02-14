package su.afk.kemonos.profile.presenter.register.util

import androidx.annotation.StringRes
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.domain.register.ConfirmErrorCode
import su.afk.kemonos.profile.domain.register.PasswordErrorCode
import su.afk.kemonos.profile.domain.register.UsernameErrorCode

@StringRes
internal fun usernameErrorRes(code: UsernameErrorCode) = when (code) {
    UsernameErrorCode.EMPTY -> R.string.login_username_empty
}

@StringRes
internal fun passwordErrorRes(code: PasswordErrorCode) = when (code) {
    PasswordErrorCode.TOO_SHORT -> R.string.register_password_too_short
}

@StringRes
internal fun confirmErrorRes(code: ConfirmErrorCode) = when (code) {
    ConfirmErrorCode.NOT_EQUAL -> R.string.register_confirm_not_equal
}
