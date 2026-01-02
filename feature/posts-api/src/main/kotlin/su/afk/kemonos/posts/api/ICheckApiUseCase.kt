package su.afk.kemonos.posts.api

import su.afk.kemonos.posts.api.apiCheck.ApiCheckResult

interface ICheckApiUseCase {
    suspend operator fun invoke(): ApiCheckResult
}