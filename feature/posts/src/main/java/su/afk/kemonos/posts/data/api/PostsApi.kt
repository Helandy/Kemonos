package su.afk.kemonos.posts.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import su.afk.kemonos.data.dto.PostUnifiedDto
import su.afk.kemonos.data.dto.onlyhaven.OnlyHavenPostsPageDto
import su.afk.kemonos.posts.data.dto.onlyhaven.OnlyHavenDmsPageDto
import su.afk.kemonos.network.textInterceptor.HeaderText
import su.afk.kemonos.posts.data.dto.PostsDto
import su.afk.kemonos.posts.data.dto.dms.DmsPageDto
import su.afk.kemonos.posts.data.dto.hashLookup.HashLookupDto
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

    @GET("v1/posts")
    @HeaderText
    suspend fun getPawchivePosts(
        @Query("o") offset: Int? = null,
        @Query("q") search: String? = null,
        @Query("tag") tag: String? = null,
    ): Response<List<PostUnifiedDto>>

    /**
     * OnlyHaven: {total, posts[]}, пагинация o/n, тегов нет.
     *
     * [sort] = "popular" даёт раздел популярного — это сортировка ленты
     * по числу закладок, периодов и дат у неё нет.
     */
    @GET("v1/posts")
    suspend fun getOnlyHavenPosts(
        @Query("o") offset: Int? = null,
        @Query("n") limit: Int? = null,
        @Query("q") search: String? = null,
        @Query("sort") sort: String? = null,
    ): Response<OnlyHavenPostsPageDto>

    /** OnlyHaven: глобальная лента личных сообщений. */
    @GET("v1/dms")
    suspend fun getOnlyHavenDms(
        @Query("o") offset: Int? = null,
        @Query("n") limit: Int? = null,
        @Query("q") search: String? = null,
    ): Response<OnlyHavenDmsPageDto>

    @GET("v1/dms")
    @HeaderText
    suspend fun getDms(
        @Query("o") offset: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("q") query: String? = null,
    ): Response<DmsPageDto>

    @GET("v1/posts/popular")
    @HeaderText
    suspend fun getPopularPosts(
        @Query("date") date: String? = null,
        @Query("period") period: PeriodDto = PeriodDto.RECENT,
        @Query("o") offset: Int? = null,
    ): Response<PopularPostsDto>

    /** Pawchive не отдаёт популярное через JSON API — только HTML-страницей в корне сайта */
    @GET("/posts/popular")
    suspend fun getPawchivePopularHtml(
        @Query("date") date: String? = null,
        @Query("period") period: PeriodDto = PeriodDto.RECENT,
        @Query("o") offset: Int? = null,
    ): Response<ResponseBody>

    @GET("v1/posts/tags")
    @HeaderText
    suspend fun getTags(): Response<List<TagsDto>>

    @GET("v1/posts/random")
    @HeaderText
    suspend fun getRandomPost(): Response<RandomDto>

    @GET("v1/search_hash/{hash}")
    @HeaderText
    suspend fun searchHash(
        @Path("hash") hash: String,
    ): Response<HashLookupDto>
}
