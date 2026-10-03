package su.afk.kemonos.creatorPost.domain.media

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.mediaUrlScheme
import su.afk.kemonos.storage.api.repository.media.IStorageMediaInfoRepository
import su.afk.kemonos.ui.uiUtils.format.buildFileUrl
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Получение информации о видео файле
 * - Продолжительность
 * - Размер
 * - Сохранение в бд
 * */
internal class GetSelfMediaMetaUseCase @Inject constructor(
    private val mediaProbe: IRemoteMediaProbe,
    private val infoCache: IStorageMediaInfoRepository,
) {
    private val inFlight = ConcurrentHashMap<String, Deferred<MediaInfo>>()

    suspend operator fun invoke(
        site: SelectedSite,
        server: String,
        path: String,
    ): MediaInfo = coroutineScope {
        val key = "${site.name}|$path"
        val mediaUrl = buildFileUrl(server, path, site.mediaUrlScheme)

        val deferred = inFlight.computeIfAbsent(key) {
            async {
                try {
                    val cachedInfo = infoCache.get(site, path)

                    val durationMs = cachedInfo?.durationMs?.takeIf { it >= 0 } ?: mediaProbe.durationMs(mediaUrl)
                    val sizeBytes = cachedInfo?.sizeBytes?.takeIf { it >= 0 } ?: mediaProbe.sizeBytes(mediaUrl)

                    val info = MediaInfo(durationMs = durationMs, sizeBytes = sizeBytes)
                    infoCache.upsert(site, path, info)

                    info
                } finally {
                    inFlight.remove(key)
                }
            }
        }

        deferred.await()
    }
}
