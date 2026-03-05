package su.afk.kemonos.creatorPost.domain.repository

import su.afk.kemonos.creatorPost.domain.model.file.FileByHashDomain
import su.afk.kemonos.creatorPost.domain.model.file.FileByPathDomain

internal interface IFileRepository {
    suspend fun getFileByHash(fileHash: String): FileByHashDomain
    suspend fun getFileByPath(path: String): FileByPathDomain
}
