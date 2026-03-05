package su.afk.kemonos.posts.domain.repository

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.apiCheck.ApiCheckForAllSitesResult

interface ICheckApiRepository {
    suspend fun getApiCheckForSites(sitesToCheck: Set<SelectedSite>): ApiCheckForAllSitesResult
}
