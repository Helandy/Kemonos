package su.afk.kemonos.profile.domain.register

import su.afk.kemonos.domain.models.ErrorItem

enum class UsernameErrorCode { EMPTY }
enum class PasswordErrorCode { TOO_SHORT }
enum class ConfirmErrorCode { NOT_EQUAL }

/**
 * То, что возвращает именно use-case:
 * - ValidationError — локальные ошибки ввода
 * - Success / Error — результат вызова репозитория
 */
sealed interface RegisterResult {
    data object Success : RegisterResult

    data class ValidationError(
        val usernameError: UsernameErrorCode? = null,
        val passwordError: PasswordErrorCode? = null,
        val confirmError: ConfirmErrorCode? = null,
    ) : RegisterResult

    data class Error(val error: ErrorItem) : RegisterResult
}

sealed interface RegisterRemoteResult {
    data object Success : RegisterRemoteResult
    data class Error(val error: ErrorItem) : RegisterRemoteResult
}