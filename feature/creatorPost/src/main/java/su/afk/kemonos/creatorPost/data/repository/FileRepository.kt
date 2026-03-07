package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.creatorPost.data.api.FileInfoApi
import su.afk.kemonos.creatorPost.data.dto.file.FileInfoRequestDto
import su.afk.kemonos.creatorPost.data.dto.file.FileInfoResponseDto.Companion.toMediaInfo
import su.afk.kemonos.creatorPost.domain.file.model.FileByHashDomain
import su.afk.kemonos.creatorPost.domain.file.model.FileByPathDomain
import su.afk.kemonos.creatorPost.domain.repository.IFileRepository
import javax.inject.Inject

internal class FileRepository @Inject constructor(
    private val fileInfoApi: FileInfoApi,
) : IFileRepository {

    override suspend fun getFileByHash(fileHash: String): FileByHashDomain? {
        return null
    }

    override suspend fun getFileByPath(path: String): FileByPathDomain? {
        return null
    }

    override suspend fun getRemoteFileInfo(site: String, server: String?, path: String): MediaInfo {
        return fileInfoApi.getFileInfo(
            data = FileInfoRequestDto(
                site = site,
                server = server,
                path = path,
            )
        ).toMediaInfo()
    }
}
