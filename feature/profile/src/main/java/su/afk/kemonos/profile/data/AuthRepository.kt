package su.afk.kemonos.profile.data

import retrofit2.HttpException
import su.afk.kemonos.auth.ClearAuthUseCase
import su.afk.kemonos.auth.SaveAuthSiteUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.extractBackendMessage
import su.afk.kemonos.network.util.safeString
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.profile.data.api.AuthenticationApi
import su.afk.kemonos.profile.data.dto.RegisterDto
import su.afk.kemonos.profile.data.dto.login.LoginDto
import su.afk.kemonos.profile.data.dto.login.LoginResponseDto.Companion.toDomain
import su.afk.kemonos.profile.domain.login.LoginRemoteResult
import su.afk.kemonos.profile.domain.register.RegisterRemoteResult
import su.afk.kemonos.profile.utils.extractSessionCookie
import javax.inject.Inject

interface IAuthRepository {
    suspend fun register(username: String, password: String): RegisterRemoteResult
    suspend fun login(username: String, password: String): LoginRemoteResult
    suspend fun logout(): Boolean
}

internal class AuthRepository @Inject constructor(
    private val api: AuthenticationApi,
    private val saveAuthSiteUseCase: SaveAuthSiteUseCase,
    private val selectedSiteProvider: ISelectedSiteUseCase,
    private val clearAuthUseCase: ClearAuthUseCase,
) : IAuthRepository {

    private fun currentSite(): SelectedSite = selectedSiteProvider.getSite()

    override suspend fun register(
        username: String,
        password: String
    ): RegisterRemoteResult {
        val response = api.register(
            RegisterDto(
                username = username,
                password = password,
                confirmPassword = password,
            )
        )

        if (response.isSuccessful) {
            return RegisterRemoteResult.Success
        }

        val code = response.code()
        if (code == 400 || code == 401) {
            val body = response.errorBody()?.safeString()
            val message = body?.extractBackendMessage() ?: "Registration failed"

            val req = response.raw().request

            return RegisterRemoteResult.Error(
                ErrorItem(
                    title = "Registration error",
                    message = message,
                    code = code,
                    url = req.url.toString(),
                    method = req.method,
                    body = body,
                )
            )
        }

        throw HttpException(response)
    }

    override suspend fun login(username: String, password: String): LoginRemoteResult {
        val site = currentSite()

        val response = api.login(LoginDto(username = username, password = password))

        if (response.isSuccessful) {
            val body = response.body()
                ?: run {
                    val req = response.raw().request
                    return LoginRemoteResult.Error(
                        ErrorItem(
                            title = "Login failed",
                            message = "Empty response body.",
                            code = response.code(),
                            url = req.url.toString(),
                            method = req.method,
                        )
                    )
                }

            val user: Login = body.toDomain()

            val session: String? = extractSessionCookie(response.headers())
            if (session.isNullOrBlank()) {
                val req = response.raw().request
                return LoginRemoteResult.Error(
                    ErrorItem(
                        title = "Login failed",
                        message = "Session cookie is missing.",
                        code = response.code(),
                        url = req.url.toString(),
                        method = req.method,
                    )
                )
            }

            saveAuthSiteUseCase(site = site, session = session, user = user)

            return LoginRemoteResult.Success
        }

        val code = response.code()
        if (code == 400 || code == 401) {
            val body = response.errorBody()?.safeString()
            val message = body?.extractBackendMessage()
                ?: if (code == 401) "Invalid username or password." else "Login failed."

            val req = response.raw().request
            return LoginRemoteResult.Error(
                ErrorItem(
                    title = "Login failed",
                    message = message,
                    code = code,
                    url = req.url.toString(),
                    method = req.method,
                    body = body,
                )
            )
        }

        throw HttpException(response)
    }

    override suspend fun logout(): Boolean {
        val site = currentSite()
        val response = api.logout()
        return if (response.isSuccessful && response.body() == true) {
            clearAuthUseCase(site)
            true
        } else {
            false
        }
    }
}