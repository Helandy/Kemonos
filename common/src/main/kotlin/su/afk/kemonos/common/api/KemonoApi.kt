package su.afk.kemonos.common.api

import retrofit2.Response
import retrofit2.http.GET
import su.afk.kemonos.network.creators.HeaderText

internal interface KemonoApi {

    /** Check api */
    @HeaderText
    @GET("v1/posts")
    suspend fun checkApiGetPosts(): Response<Any>
}