package su.afk.kemonos.creators.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import su.afk.kemonos.creators.data.dto.onlyhaven.OnlyHavenCreatorsPageDto
import su.afk.kemonos.creators.data.dto.CreatorsDto
import su.afk.kemonos.creators.data.dto.RandomCreatorDto
import su.afk.kemonos.network.textInterceptor.HeaderText

internal interface CreatorsApi {

    /** Список всех авторов */
    @HeaderText
    @GET("v1/creators")
    suspend fun getCreators(): Response<List<CreatorsDto>>

    /**
     * OnlyHaven: авторов больше 10к, эндпоинт постраничный и умеет
     * серверные поиск/фильтр/сортировку — выкачивать список целиком нельзя.
     */
    @GET("v1/creators")
    suspend fun getOnlyHavenCreators(
        @Query("o") offset: Int? = null,
        @Query("n") limit: Int? = null,
        @Query("q") query: String? = null,
        @Query("service") service: String? = null,
        @Query("sort") sort: String? = null,
    ): Response<OnlyHavenCreatorsPageDto>

    /** Получить рандомного автора */
    @HeaderText
    @GET("v1/artists/random")
    suspend fun randomCreator(): Response<RandomCreatorDto>
}