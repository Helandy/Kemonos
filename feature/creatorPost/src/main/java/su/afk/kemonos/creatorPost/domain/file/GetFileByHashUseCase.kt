package su.afk.kemonos.creatorPost.domain.file

import su.afk.kemonos.creatorPost.domain.file.model.FileByHashDomain
import su.afk.kemonos.creatorPost.domain.repository.IFileRepository
import javax.inject.Inject

internal class GetFileByHashUseCase @Inject constructor(
    private val repository: IFileRepository,
) {
    suspend operator fun invoke(fileHash: String): FileByHashDomain? {
        return repository.getFileByHash(fileHash)
    }
}
