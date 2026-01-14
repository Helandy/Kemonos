package su.afk.kemonos.posts.api

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.apiCheck.ApiCheckForAllSitesResult

interface ICheckApiUseCase {
    suspend operator fun invoke(sitesToCheck: Set<SelectedSite>): ApiCheckForAllSitesResult
}