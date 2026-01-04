package su.afk.kemonos.posts.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import su.afk.kemonos.network.textInterceptor.HeaderText
import su.afk.kemonos.posts.data.dto.PostsDto
import su.afk.kemonos.posts.data.dto.popular.request.PeriodDto
import su.afk.kemonos.posts.data.dto.popular.response.PopularPostsDto
import su.afk.kemonos.posts.data.dto.random.RandomDto
import su.afk.kemonos.posts.data.dto.tags.TagsDto

internal interface PostsApi {

    @GET("v1/posts")
    @HeaderText
    suspend fun getPosts(
        @Query("o") offset: Int? = null,
        @Query("q") search: String? = null,
        @Query("tag") tag: String? = null,
    ): Response<PostsDto>

    @GET("v1/posts/popular")
    @HeaderText
    suspend fun getPopularPosts(
        @Query("date") date: String? = null,
        @Query("period") period: PeriodDto = PeriodDto.RECENT,
        @Query("o") offset: Int? = null,
    ): Response<PopularPostsDto>

    @GET("v1/posts/tags")
    @HeaderText
    suspend fun getTags(): Response<List<TagsDto>>

    @GET("v1/posts/random")
    @HeaderText
    suspend fun getRandomPost(): Response<RandomDto>
}