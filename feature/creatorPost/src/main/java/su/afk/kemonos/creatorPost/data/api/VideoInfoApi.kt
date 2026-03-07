package su.afk.kemonos.creatorPost.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import su.afk.kemonos.creatorPost.data.dto.videoInfo.VideoInfoDto
import su.afk.kemonos.creatorPost.data.dto.videoInfo.VideoInfoResponseDto
import su.afk.kemonos.network.versionInterceptor.VersionHeader

internal interface VideoInfoApi {

    @POST("/api/video/info")
    @VersionHeader
    suspend fun getVideoInfo(
        @Body data: VideoInfoDto,
    ): VideoInfoResponseDto
}