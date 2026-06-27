package su.afk.kemonos.creators.domain.random

import su.afk.kemonos.creators.domain.repository.ICreatorsRepository
import su.afk.kemonos.domain.SelectedSite
import javax.inject.Inject

class RandomCreatorUseCase @Inject constructor(
    private val creatorsRepository: ICreatorsRepository
) {
    suspend operator fun invoke(site: SelectedSite): RandomCreatorModel = creatorsRepository.randomCreator(site)
}
