package su.afk.kemonos.creatorPost.domain.file

import su.afk.kemonos.creatorPost.domain.file.model.FileByPathDomain
import su.afk.kemonos.creatorPost.domain.repository.IFileRepository
import javax.inject.Inject

internal class GetFileByPathUseCase @Inject constructor(
    private val repository: IFileRepository,
) {
    suspend operator fun invoke(path: String): FileByPathDomain? {
        return repository.getFileByPath(path)
    }
}
