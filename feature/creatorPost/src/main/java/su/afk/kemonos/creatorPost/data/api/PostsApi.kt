package su.afk.kemonos.creatorPost.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import su.afk.kemonos.core.api.domain.net.intercaptiors.HeaderText
import su.afk.kemonos.creatorPost.data.dto.comments.ProfilePostCommentsDto
import su.afk.kemonos.creatorPost.data.dto.profilePost.PostResponseDto

internal interface PostsApi {

    /** Comments к Посту */
    @HeaderText
    @GET("v1/{service}/user/{id}/post/{postId}/comments")
    suspend fun getProfilePostComments(
        @Path("service") service: String,
        @Path("id") id: String,
        @Path("postId") postId: String,
    ): Response<List<ProfilePostCommentsDto>>

    /** Пост */
    @HeaderText
    @GET("v1/{service}/user/{id}/post/{postId}")
    suspend fun getProfilePost(
        @Path("service") service: String,
        @Path("id") id: String,
        @Path("postId") postId: String,
    ): Response<PostResponseDto>
}