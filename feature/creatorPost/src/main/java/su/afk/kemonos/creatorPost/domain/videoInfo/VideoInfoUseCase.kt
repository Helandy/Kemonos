package su.afk.kemonos.creatorPost.domain.videoInfo

import su.afk.kemonos.creatorPost.domain.repository.IVideoInfoRepository
import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo
import javax.inject.Inject

internal class VideoInfoUseCase @Inject constructor(
    private val repository: IVideoInfoRepository
) {
    suspend operator fun invoke(site: String, server: String?, path: String): VideoInfo {
        return repository.getVideoInfo(
            site = site,
            server = server,
            path = path
        )
    }
}
