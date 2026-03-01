package su.afk.kemonos.creators.domain.repository

import su.afk.kemonos.creators.domain.random.RandomCreatorModel
import su.afk.kemonos.domain.models.creator.Creators

interface ICreatorsRepository {
    suspend fun getCreators(): List<Creators>
    suspend fun refreshCreatorsIfNeeded(): Boolean
    suspend fun randomCreator(): RandomCreatorModel
}