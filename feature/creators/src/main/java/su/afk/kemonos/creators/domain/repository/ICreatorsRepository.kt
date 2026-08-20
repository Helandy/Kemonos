package su.afk.kemonos.creators.domain.repository

import su.afk.kemonos.creators.domain.random.RandomCreatorModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort

interface ICreatorsRepository {
    suspend fun getCreators(site: SelectedSite): List<Creators>
    suspend fun refreshCreatorsIfNeeded(site: SelectedSite): Boolean
    suspend fun randomCreator(site: SelectedSite): RandomCreatorModel

    /**
     * Страница авторов прямо из сети.
     *
     * Для источников с [su.afk.kemonos.domain.SiteCapabilities.bulkCreatorList] = false,
     * где список нельзя выкачать целиком, а поиск и сортировка есть на сервере.
     */
    suspend fun getCreatorsPage(
        site: SelectedSite,
        service: String?,
        query: String,
        sort: CreatorsSort,
        limit: Int,
        offset: Int,
    ): List<Creators>
}
