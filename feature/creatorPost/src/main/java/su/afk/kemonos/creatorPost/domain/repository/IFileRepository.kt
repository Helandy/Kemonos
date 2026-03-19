package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo
import su.afk.kemonos.creatorPost.domain.file.model.FileByHashDomain
import su.afk.kemonos.creatorPost.domain.file.model.FileByPathDomain

internal interface IFileRepository {
    suspend fun getFileByHash(fileHash: String): FileByHashDomain?
    suspend fun getFileByPath(path: String): FileByPathDomain?
    suspend fun getRemoteFileInfo(site: String, server: String?, path: String): MediaInfo
}
