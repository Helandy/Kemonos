package su.afk.kemonos.creators.data.api

import retrofit2.Response
import retrofit2.http.GET
import su.afk.kemonos.creators.data.dto.CreatorsDto
import su.afk.kemonos.creators.data.dto.RandomCreatorDto
import su.afk.kemonos.network.textInterceptor.HeaderText

internal interface CreatorsApi {

    @HeaderText
    @GET("v1/creators")
    suspend fun getCreators(): Response<List<CreatorsDto>>

    @HeaderText
    @GET("v1/artists/random")
    suspend fun randomCreator(): Response<RandomCreatorDto>
}