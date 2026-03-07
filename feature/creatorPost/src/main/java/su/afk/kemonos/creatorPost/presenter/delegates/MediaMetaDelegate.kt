package su.afk.kemonos.creatorPost.presenter.delegates

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.creatorPost.domain.file.FileInfoUseCase
import su.afk.kemonos.creatorPost.domain.media.GetSelfMediaMetaUseCase
import su.afk.kemonos.creatorPost.domain.media.model.CommonMediaInfo
import su.afk.kemonos.creatorPost.domain.videoInfo.VideoInfoUseCase
import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo
import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo.Companion.toStorageMediaInfo
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.selectedSiteByService
import su.afk.kemonos.storage.api.repository.media.IStorageMediaInfoRepository
import javax.inject.Inject

internal class MediaMetaDelegateNew @Inject constructor(
    private val videoInfoUseCase: VideoInfoUseCase,
    private val fileInfoUseCase: FileInfoUseCase,
    private val getSelfMediaInfoUseCase: GetSelfMediaMetaUseCase,
    private val domainResolver: IDomainResolver,
    private val storageMediaInfo: IStorageMediaInfoRepository,
) {
    /** Получение информации о видео включая превью */
    suspend fun getVideoInfo(isRemote: Boolean, service: String, server: String?, path: String): CommonMediaInfo {
        val site = domainResolver.selectedSiteByService(service)
        storageMediaInfo.get(site, path)?.let { cached ->
            /** Если инфа о видео self а не remote, то дернем api мб появился кэш и превью */
            val cachedVideoInfo = cached.toCachedVideoInfoOrNull(path = path, isRemote = isRemote)
            if (!isRemote || cachedVideoInfo != null) {
                return CommonMediaInfo(
                    videoInfo = cachedVideoInfo,
                    mediaInfo = cached,
                )
            }

            // If only self metadata is cached, still try the remote video/info endpoint
            // so preview-capable metadata can be filled in later.
        }

        return when (isRemote) {
            true -> getRemoteMediaInfo(
                service = service,
                server = server,
                path = path,
            )

            false -> getSelfMediaInfo(
                service = service,
                path = path,
            )
        }
    }

    /** Получение состояние о медиа Видео/Аудио */
    suspend fun getRemoteMediaInfo(service: String, server: String?, path: String): CommonMediaInfo {
        val site = domainResolver.baseUrlByService(service)
        val selectedSite = domainResolver.selectedSiteByService(service)

        val result = videoInfoUseCase(
            site = site,
            server = server,
            path = path,
        )

        result.let { info ->
            storageMediaInfo.upsert(
                site = selectedSite,
                path = path,
                info = info.toStorageMediaInfo(),
            )
        }

        return CommonMediaInfo(
            videoInfo = result,
        )
    }

    suspend fun getAudioInfo(isRemote: Boolean, service: String, server: String?, path: String): CommonMediaInfo {
        val site = domainResolver.selectedSiteByService(service)
        storageMediaInfo.get(site, path)?.let { cached ->
            return CommonMediaInfo(
                mediaInfo = cached,
            )
        }

        return when (isRemote) {
            true -> getRemoteAudioInfo(
                service = service,
                server = server,
                path = path,
            )

            false -> getSelfMediaInfo(
                service = service,
                path = path,
            )
        }
    }

    private suspend fun getRemoteAudioInfo(service: String, server: String?, path: String): CommonMediaInfo {
        val site = domainResolver.baseUrlByService(service)
        val selectedSite = domainResolver.selectedSiteByService(service)

        val result = fileInfoUseCase(
            site = site,
            server = server,
            path = path,
        )

        result.let { info ->
            storageMediaInfo.upsert(
                site = selectedSite,
                path = path,
                info = info,
            )
        }

        return CommonMediaInfo(
            mediaInfo = result,
        )
    }

    /** Локальное получение состояние о Видео/Аудио  */
    suspend fun getSelfMediaInfo(service: String, path: String): CommonMediaInfo {
        val site = domainResolver.selectedSiteByService(service)
        val server = domainResolver.baseUrlByService(service)

        val result = getSelfMediaInfoUseCase(
            site = site,
            server = server,
            path = path,
        )

        return CommonMediaInfo(
            mediaInfo = result,
        )
    }

    private fun MediaInfo.toCachedVideoInfoOrNull(path: String, isRemote: Boolean): VideoInfo? {
        if (!isRemote) return null

        val cachedDurationSeconds = durationSeconds ?: return null
        val cachedStatusCode = lastStatusCode ?: return null

        return VideoInfo(
            path = path,
            sizeBytes = sizeBytes,
            durationSeconds = cachedDurationSeconds,
            lastStatusCode = cachedStatusCode,
        )
    }
}
