package su.afk.kemonos.creatorProfile.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import su.afk.kemonos.creatorProfile.data.dto.profileCommunity.DiscordChannelMessageDto
import su.afk.kemonos.creatorProfile.data.dto.profileCommunity.DiscordServerResponseDto
import su.afk.kemonos.network.textInterceptor.HeaderText

internal interface DiscordApi {

    @HeaderText
    @GET("v1/discord/server/{serverId}")
    suspend fun getDiscordServer(
        @Path("serverId") serverId: String,
    ): Response<DiscordServerResponseDto>

    @HeaderText
    @GET("v1/discord/channel/{channelId}")
    suspend fun getDiscordChannelMessages(
        @Path("channelId") channelId: String,
        @Query("o") offset: Int? = null,
    ): Response<List<DiscordChannelMessageDto>>
}
