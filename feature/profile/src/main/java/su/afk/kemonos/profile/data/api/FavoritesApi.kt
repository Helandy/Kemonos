package su.afk.kemonos.profile.data.api

import retrofit2.Response
import retrofit2.http.GET
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
}