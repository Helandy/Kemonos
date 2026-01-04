package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.posts.api.ICheckApiUseCase
import su.afk.kemonos.posts.data.CheckApiRepository
import javax.inject.Inject

internal class CheckApiUseCase @Inject constructor(
    private val checkApiRepository: CheckApiRepository
) : ICheckApiUseCase {
    override suspend operator fun invoke() = checkApiRepository.getApiCheckForAllSites()
}