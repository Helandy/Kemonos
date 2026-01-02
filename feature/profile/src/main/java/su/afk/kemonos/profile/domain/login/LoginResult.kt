package su.afk.kemonos.profile.domain.login

import su.afk.kemonos.domain.models.ErrorItem

enum class LoginUsernameErrorCode { EMPTY }
enum class LoginPasswordErrorCode { EMPTY }

/**
 * То, что возвращает именно use-case:
 * - ValidationError — локальные ошибки ввода
 * - Success / Error — результат вызова репозитория
 */
sealed interface LoginResult {
    data object Success : LoginResult

    data class ValidationError(
        val usernameError: LoginUsernameErrorCode? = null,
        val passwordError: LoginPasswordErrorCode? = null,
    ) : LoginResult

    data class Error(val error: ErrorItem) : LoginResult
}

sealed interface LoginRemoteResult {
    data object Success : LoginRemoteResult
    data class Error(val error: ErrorItem) : LoginRemoteResult
}