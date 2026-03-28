package su.afk.kemonos.profile.data.repository

import retrofit2.HttpException
import retrofit2.Response
import su.afk.kemonos.auth.ClearAuthUseCase
import su.afk.kemonos.auth.SaveAuthSiteUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AuthUser
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
import su.afk.kemonos.profile.domain.repository.IAuthRepository
import su.afk.kemonos.profile.utils.extractSessionCookie
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val api: AuthenticationApi,
    private val saveAuthSiteUseCase: SaveAuthSiteUseCase,
    private val selectedSiteProvider: ISelectedSiteUseCase,
    private val clearAuthUseCase: ClearAuthUseCase,
) : IAuthRepository {

    /** Текущий сайт, с которым работает auth-flow в этот момент. */
    private fun currentSite(): SelectedSite = selectedSiteProvider.getSite()

    /** Регистрация нового пользователя. */
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
            if (response.body() == false) {
                return RegisterRemoteResult.Error(
                    response.toErrorItem(
                        title = "Registration error",
                        defaultMessage = "Registration failed",
                    )
                )
            }
            return RegisterRemoteResult.Success
        }

        val code = response.code()
        if (code in 400..499) {
            return RegisterRemoteResult.Error(
                response.toErrorItem(
                    title = "Registration error",
                    defaultMessage = if (code == 409) {
                        "Username already exists"
                    } else {
                        "Registration failed"
                    },
                )
            )
        }

        throw HttpException(response)
    }

    /** Логин пользователя, извлечение session cookie и сохранение auth-состояния. */
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

            saveAuthSiteUseCase(site = site, session = session, user = user.toAuthUser())

            return LoginRemoteResult.Success
        }

        val code = response.code()
        if (code in 400..499) {
            return LoginRemoteResult.Error(
                response.toErrorItem(
                    title = "Login failed",
                    defaultMessage = if (code == 401) {
                        "Invalid username or password."
                    } else {
                        "Login failed."
                    },
                )
            )
        }

        throw HttpException(response)
    }

    /** Logout на сервере и очистка локальной auth-сессии для текущего сайта. */
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

    /** Унифицированный маппинг retrofit-ошибки в доменный ErrorItem. */
    private fun <T> Response<T>.toErrorItem(
        title: String,
        defaultMessage: String,
    ): ErrorItem {
        val body = errorBody()?.safeString()
        val request = raw().request

        return ErrorItem(
            title = title,
            message = body?.extractBackendMessage() ?: defaultMessage,
            code = code(),
            url = request.url.toString(),
            method = request.method,
            requestId = headers()["x-request-id"] ?: headers()["X-Request-Id"],
            body = body,
        )
    }

    private fun Login.toAuthUser(): AuthUser = AuthUser(
        id = id,
        username = username,
        createdAt = createdAt,
        role = role,
    )
}
