package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.data.api.FileApi
import su.afk.kemonos.creatorPost.data.dto.file.FileByHashResponseDto.Companion.toDomain
import su.afk.kemonos.creatorPost.data.dto.file.FileByPathResponseDto.Companion.toDomain
import su.afk.kemonos.creatorPost.domain.model.file.FileByHashDomain
import su.afk.kemonos.creatorPost.domain.model.file.FileByPathDomain
import su.afk.kemonos.network.util.call
import javax.inject.Inject

internal interface IFileRepository {
    suspend fun getFileByHash(fileHash: String): FileByHashDomain
    suspend fun getFileByPath(path: String): FileByPathDomain
}

internal class FileRepository @Inject constructor(
    private val api: FileApi,
) : IFileRepository {

    override suspend fun getFileByHash(fileHash: String): FileByHashDomain {
        return api.getFileByHash(fileHash).call { dto -> dto.toDomain() }
    }

    override suspend fun getFileByPath(path: String): FileByPathDomain {
        return api.getFileByPath(path).call { dto -> dto.toDomain() }
    }
}
