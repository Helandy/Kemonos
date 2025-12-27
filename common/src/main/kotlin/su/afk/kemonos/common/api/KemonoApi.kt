package su.afk.kemonos.common.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import su.afk.kemonos.common.data.profile.ProfileDto
import su.afk.kemonos.core.api.domain.net.intercaptiors.HeaderText

internal interface KemonoApi {

    /** Check api */
    @HeaderText
    @GET("v1/posts")
    suspend fun checkApiGetPosts(): Response<Any>

    /** Профиль */
    @HeaderText
    @GET("v1/{service}/user/{id}/profile")
    suspend fun getProfile(
        @Path("service") service: String,
        @Path("id") id: String
    ): Response<ProfileDto>
}