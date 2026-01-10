package su.afk.kemonos.commonscreen.errorScreen

import su.afk.kemonos.domain.models.ErrorItem

internal data class ErrorScreenState(
    val error: ErrorItem? = null,
)