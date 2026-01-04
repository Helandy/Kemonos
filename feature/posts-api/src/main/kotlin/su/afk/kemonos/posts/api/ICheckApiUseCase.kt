package su.afk.kemonos.posts.api

import su.afk.kemonos.posts.api.apiCheck.ApiCheckForAllSitesResult

interface ICheckApiUseCase {
    suspend operator fun invoke(): ApiCheckForAllSitesResult
}