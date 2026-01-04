package su.afk.kemonos.profile.data.api

import retrofit2.Response
import retrofit2.http.GET
import su.afk.kemonos.network.auth.AuthCookie
import su.afk.kemonos.network.textInterceptor.HeaderText
import su.afk.kemonos.profile.data.dto.account.AccountDto

internal interface AccountApi {

    @AuthCookie
    @HeaderText
    @GET("v1/account")
    suspend fun getAccount(): Response<AccountDto>
}