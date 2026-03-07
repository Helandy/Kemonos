package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo

internal interface IVideoInfoRepository {

    suspend fun getVideoInfo(site: String, server: String?, path: String): VideoInfo
}
