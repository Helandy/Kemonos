package su.afk.kemonos.creators.domain.random

import su.afk.kemonos.creators.domain.repository.ICreatorsRepository
import javax.inject.Inject

class RandomCreatorUseCase @Inject constructor(
    private val creatorsRepository: ICreatorsRepository
) {
    suspend operator fun invoke(): RandomCreatorModel = creatorsRepository.randomCreator()
}
