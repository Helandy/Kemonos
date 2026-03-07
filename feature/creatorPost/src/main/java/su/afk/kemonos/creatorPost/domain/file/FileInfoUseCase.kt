package su.afk.kemonos.creatorPost.domain.file

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.creatorPost.domain.repository.IFileRepository
import javax.inject.Inject

internal class FileInfoUseCase @Inject constructor(
    private val repository: IFileRepository,
) {
    suspend operator fun invoke(site: String, server: String?, path: String): MediaInfo {
        return repository.getRemoteFileInfo(
            site = site,
            server = server,
            path = path,
        )
    }
}
