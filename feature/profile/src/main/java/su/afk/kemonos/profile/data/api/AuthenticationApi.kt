package su.afk.kemonos.profile.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import su.afk.kemonos.profile.data.dto.RegisterDto
import su.afk.kemonos.profile.data.dto.login.LoginDto
import su.afk.kemonos.profile.data.dto.login.LoginResponseDto

internal interface AuthenticationApi {

    @POST("v1/authentication/register")
    suspend fun register(
        @Body data: RegisterDto,
    ): Response<Boolean>

    @POST("v1/authentication/login")
    suspend fun login(
        @Body data: LoginDto,
    ): Response<LoginResponseDto>

    @POST("v1/authentication/logout")
    suspend fun logout(): Response<Boolean>
}