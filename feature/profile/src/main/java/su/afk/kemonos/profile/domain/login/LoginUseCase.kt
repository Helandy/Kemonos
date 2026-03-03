package su.afk.kemonos.profile.domain.login

import su.afk.kemonos.profile.domain.IAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
) {

    /**
     * Выполняет локальную валидацию и логин.
     * Username нормализуется через trim(), чтобы не отправлять случайные пробелы по краям.
     */
    suspend operator fun invoke(
        username: String,
        password: String,
    ): LoginResult {
        val normalizedUsername = username.trim()

        val usernameError = if (normalizedUsername.isBlank()) LoginUsernameErrorCode.EMPTY else null
        val passwordError = if (password.isBlank()) LoginPasswordErrorCode.EMPTY else null

        if (usernameError != null || passwordError != null) {
            return LoginResult.ValidationError(
                usernameError = usernameError,
                passwordError = passwordError,
            )
        }

        return when (val remote = authRepository.login(normalizedUsername, password)) {
            is LoginRemoteResult.Success -> {
                LoginResult.Success
            }

            is LoginRemoteResult.Error -> {
                LoginResult.Error(remote.error)
            }
        }
    }
}
