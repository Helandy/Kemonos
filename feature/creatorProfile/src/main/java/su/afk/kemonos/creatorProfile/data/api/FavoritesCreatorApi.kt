package su.afk.kemonos.creatorProfile.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import su.afk.kemonos.network.auth.AuthCookie

internal interface FavoritesCreatorApi {

    @AuthCookie
    @POST("v1/favorites/creator/{service}/{id}")
    suspend fun addFavoriteCreator(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<Unit>

    @AuthCookie
    @DELETE("v1/favorites/creator/{service}/{id}")
    suspend fun removeFavoriteCreator(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<Unit>
}