package su.afk.kemonos.profile.domain.logout

import retrofit2.HttpException
import su.afk.kemonos.profile.data.IAuthRepository
import java.io.IOException
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
) {

    suspend operator fun invoke(): LogoutResult {
        return try {
            val success = authRepository.logout()
            if (success) {
                LogoutResult.Success
            } else {
                LogoutResult.ServerError(message = null)
            }
        } catch (e: IOException) {
            LogoutResult.NetworkError
        } catch (e: HttpException) {
            LogoutResult.ServerError(e.message())
        } catch (e: Exception) {
            LogoutResult.Unknown
        }
    }
}