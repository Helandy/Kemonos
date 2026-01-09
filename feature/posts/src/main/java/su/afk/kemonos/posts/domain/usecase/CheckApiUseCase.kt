package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.ICheckApiUseCase
import su.afk.kemonos.posts.data.ICheckApiRepository
import javax.inject.Inject

internal class CheckApiUseCase @Inject constructor(
    private val checkApiRepository: ICheckApiRepository
) : ICheckApiUseCase {
    override suspend fun invoke(sitesToCheck: Set<SelectedSite>) = checkApiRepository.getApiCheckForSites(sitesToCheck)
}