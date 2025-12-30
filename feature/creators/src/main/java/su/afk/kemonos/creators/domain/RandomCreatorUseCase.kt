package su.afk.kemonos.creators.domain

import su.afk.kemonos.creators.data.ICreatorsRepository
import su.afk.kemonos.creators.domain.model.RandomCreator
import javax.inject.Inject

class RandomCreatorUseCase @Inject constructor(
    private val creatorsRepository: ICreatorsRepository
) {
    suspend operator fun invoke(): RandomCreator = creatorsRepository.randomCreator()
}