package su.afk.kemonos.posts.api.apiCheck

import su.afk.kemonos.domain.models.ErrorItem

data class ApiCheckResult(
    val success: Boolean,
    val error: ErrorItem? = null,
)