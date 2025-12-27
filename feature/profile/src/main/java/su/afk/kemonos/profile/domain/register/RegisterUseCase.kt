package su.afk.kemonos.profile.domain.register

import su.afk.kemonos.profile.data.IAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
) {

    suspend operator fun invoke(
        username: String,
        password: String,
        confirm: String,
    ): RegisterResult {
        val usernameError = if (username.isBlank()) UsernameErrorCode.EMPTY else null
        val passwordError = if (password.length < 6) PasswordErrorCode.TOO_SHORT else null
        val confirmError = if (confirm != password) ConfirmErrorCode.NOT_EQUAL else null

        if (usernameError != null || passwordError != null || confirmError != null) {
            return RegisterResult.ValidationError(
                usernameError = usernameError,
                passwordError = passwordError,
                confirmError = confirmError,
            )
        }

        return when (val remote = authRepository.register(username, password)) {
            is RegisterRemoteResult.Success -> {
                RegisterResult.Success
            }

            is RegisterRemoteResult.Error -> {
                RegisterResult.Error(remote.error)
            }
        }
    }
}
