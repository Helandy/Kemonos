package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.data.api.VideoInfoApi
import su.afk.kemonos.creatorPost.data.dto.videoInfo.VideoInfoDto
import su.afk.kemonos.creatorPost.data.dto.videoInfo.VideoInfoResponseDto.Companion.toDomain
import su.afk.kemonos.creatorPost.domain.repository.IVideoInfoRepository
import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo
import javax.inject.Inject

internal class VideoInfoRepository @Inject constructor(
    private val api: VideoInfoApi,
) : IVideoInfoRepository {

    override suspend fun getVideoInfo(
        site: String,
        server: String?,
        path: String
    ): VideoInfo {
        return api.getVideoInfo(
            data = VideoInfoDto(
                site = site,
                server = server,
                path = path,
            )
        ).toDomain()
    }
}
