package su.afk.kemonos.profile.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import su.afk.kemonos.data.dto.PostUnifiedDto
import su.afk.kemonos.network.auth.AuthCookie
import su.afk.kemonos.network.textInterceptor.HeaderText
import su.afk.kemonos.profile.data.dto.favorites.artist.FavoriteArtistDto

internal interface FavoritesApi {

    @AuthCookie
    @HeaderText
    @GET("v1/account/favorites")
    suspend fun getFavoriteArtists(
        @Query("type") type: String = "artist",
    ): Response<List<FavoriteArtistDto>>

    @AuthCookie
    @HeaderText
    @GET("v1/account/favorites")
    suspend fun getFavoritePosts(
        @Query("type") type: String = "post",
    ): Response<List<PostUnifiedDto>>

    @AuthCookie
    @HeaderText
    @GET("v1/account/favorites")
    suspend fun getFavoriteArtistsRaw(
        @Query("type") type: String = "artist",
    ): Response<ResponseBody>

    @AuthCookie
    @HeaderText
    @GET("v1/account/favorites")
    suspend fun getFavoritePostsRaw(
        @Query("type") type: String = "post",
    ): Response<ResponseBody>

    @AuthCookie
    @POST("v1/favorites/creator/{service}/{id}")
    suspend fun addFavoriteCreator(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<Unit>

    @AuthCookie
    @POST("v1/favorites/post/{service}/{creatorId}/{postId}")
    suspend fun addFavoritePost(
        @Path("service") service: String,
        @Path("creatorId") creatorId: String,
        @Path("postId") postId: String,
    ): Response<Unit>
}
