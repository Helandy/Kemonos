package su.afk.kemonos.storage.useCases.video

import su.afk.kemonos.creatorPost.api.domain.model.VideoInfo
import su.afk.kemonos.storage.api.video.IVideoInfoUseCase
import su.afk.kemonos.storage.repository.video.IStoreVideoInfoRepository
import javax.inject.Inject

internal class VideoInfoUseCase @Inject constructor(
    private val repo: IStoreVideoInfoRepository
) : IVideoInfoUseCase {

    override suspend fun get(name: String): VideoInfo? = repo.get(name)

    override suspend fun upsert(name: String, info: VideoInfo) = repo.upsert(name, info)

    override suspend fun clear() = repo.clear()
}