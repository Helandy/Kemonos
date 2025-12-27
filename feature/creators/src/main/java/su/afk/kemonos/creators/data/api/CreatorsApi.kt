package su.afk.kemonos.creators.data.api

import retrofit2.Response
import retrofit2.http.GET
import su.afk.kemonos.common.data.creators.CreatorsDto
import su.afk.kemonos.core.api.domain.net.intercaptiors.HeaderText

internal interface CreatorsApi {

    @HeaderText
    @GET("v1/creators")
    suspend fun getCreators(): Response<List<CreatorsDto>>

}