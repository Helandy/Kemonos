package su.afk.kemonos.profile.domain.logout

sealed interface LogoutResult {
    data object Success : LogoutResult
    data object NetworkError : LogoutResult
    data class ServerError(val message: String?) : LogoutResult
    data object Unknown : LogoutResult
}