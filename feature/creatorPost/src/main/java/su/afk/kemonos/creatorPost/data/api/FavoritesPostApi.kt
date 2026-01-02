package su.afk.kemonos.creatorPost.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import su.afk.kemonos.network.auth.AuthCookie

internal interface FavoritesPostApi {

    @AuthCookie
    @POST("v1/favorites/post/{service}/{creatorId}/{postId}")
    suspend fun addFavoritePost(
        @Path("service") service: String,
        @Path("creatorId") creatorId: String,
        @Path("postId") postId: String,
    ): Response<Unit>

    @AuthCookie
    @DELETE("v1/favorites/post/{service}/{creatorId}/{postId}")
    suspend fun removeFavoritePost(
        @Path("service") service: String,
        @Path("creatorId") creatorId: String,
        @Path("postId") postId: String,
    ): Response<Unit>
}