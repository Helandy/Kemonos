package su.afk.kemonos.commonscreen

import su.afk.kemonos.domain.domain.models.ErrorItem

internal data class ErrorScreenState(
    val error: ErrorItem? = null,
)