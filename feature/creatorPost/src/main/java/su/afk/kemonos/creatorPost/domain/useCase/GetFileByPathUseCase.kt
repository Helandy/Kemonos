package su.afk.kemonos.creatorPost.domain.useCase

import su.afk.kemonos.creatorPost.data.repository.IFileRepository
import su.afk.kemonos.creatorPost.domain.model.file.FileByPathDomain
import javax.inject.Inject

internal class GetFileByPathUseCase @Inject constructor(
    private val repository: IFileRepository,
) {
    suspend operator fun invoke(path: String): FileByPathDomain {
        return repository.getFileByPath(path)
    }
}
