package su.afk.kemonos.profile.domain.register

import su.afk.kemonos.profile.domain.repository.IAuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
) {

    /**
     * Выполняет локальную валидацию и регистрацию.
     * Username нормализуется через trim(), чтобы не отправлять случайные пробелы по краям.
     */
    suspend operator fun invoke(
        username: String,
        password: String,
        confirm: String,
    ): RegisterResult {
        val normalizedUsername = username.trim()

        val usernameError = if (normalizedUsername.isBlank()) UsernameErrorCode.EMPTY else null
        val passwordError = if (password.length < MIN_PASSWORD_LENGTH) PasswordErrorCode.TOO_SHORT else null
        val confirmError = if (confirm != password) ConfirmErrorCode.NOT_EQUAL else null

        if (usernameError != null || passwordError != null || confirmError != null) {
            return RegisterResult.ValidationError(
                usernameError = usernameError,
                passwordError = passwordError,
                confirmError = confirmError,
            )
        }

        return when (val remote = authRepository.register(normalizedUsername, password)) {
            is RegisterRemoteResult.Success -> {
                RegisterResult.Success
            }

            is RegisterRemoteResult.Error -> {
                RegisterResult.Error(remote.error)
            }
        }
    }

    private companion object {
        private const val MIN_PASSWORD_LENGTH = 6
    }
}
