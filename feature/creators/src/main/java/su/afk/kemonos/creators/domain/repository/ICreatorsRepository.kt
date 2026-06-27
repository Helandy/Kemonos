package su.afk.kemonos.creators.domain.repository

import su.afk.kemonos.creators.domain.random.RandomCreatorModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators

interface ICreatorsRepository {
    suspend fun getCreators(site: SelectedSite): List<Creators>
    suspend fun refreshCreatorsIfNeeded(site: SelectedSite): Boolean
    suspend fun randomCreator(site: SelectedSite): RandomCreatorModel
}
