package su.afk.kemonos.profile.domain.login

import su.afk.kemonos.profile.data.IAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
) {

    suspend operator fun invoke(
        username: String,
        password: String,
    ): LoginResult {
        val usernameError = if (username.isBlank()) LoginUsernameErrorCode.EMPTY else null
        val passwordError = if (password.isBlank()) LoginPasswordErrorCode.EMPTY else null

        if (usernameError != null || passwordError != null) {
            return LoginResult.ValidationError(
                usernameError = usernameError,
                passwordError = passwordError,
            )
        }

        return when (val remote = authRepository.login(username, password)) {
            is LoginRemoteResult.Success -> {
                LoginResult.Success
            }

            is LoginRemoteResult.Error -> {
                LoginResult.Error(remote.error)
            }
        }
    }
}