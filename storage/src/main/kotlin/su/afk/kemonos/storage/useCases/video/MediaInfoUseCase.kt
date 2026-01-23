package su.afk.kemonos.storage.useCases.video

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.storage.api.video.IMediaInfoUseCase
import su.afk.kemonos.storage.repository.video.IStoreVideoInfoRepository
import javax.inject.Inject

internal class MediaInfoUseCase @Inject constructor(
    private val repo: IStoreVideoInfoRepository
) : IMediaInfoUseCase {

    override suspend fun get(key: String): MediaInfo? = repo.get(key)

    override suspend fun upsert(key: String, info: MediaInfo) = repo.upsert(key, info)

    override suspend fun clear() = repo.clear()
}