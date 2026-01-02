package su.afk.kemonos.common.repository.checkApi

import su.afk.kemonos.domain.models.ErrorItem

data class ApiCheckResult(
    val success: Boolean,
    val error: ErrorItem? = null,
)
