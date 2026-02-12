package su.afk.kemonos.creatorPost.domain.useCase

import su.afk.kemonos.creatorPost.data.repository.IFileRepository
import su.afk.kemonos.creatorPost.domain.model.file.FileByHashDomain
import javax.inject.Inject

internal class GetFileByHashUseCase @Inject constructor(
    private val repository: IFileRepository,
) {
    suspend operator fun invoke(fileHash: String): FileByHashDomain {
        return repository.getFileByHash(fileHash)
    }
}
