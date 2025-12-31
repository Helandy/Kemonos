package su.afk.kemonos.main.presenter

import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.domain.domain.models.ErrorItem

data class MainState(
    val isLoading: Boolean = true,
    val error: ErrorItem? = null,

    val apiSuccess: Boolean? = null,

    /** Значения из Prefs (текущие, «истинные») */
    val kemonoUrl: String = "",
    val coomerUrl: String = "",

    val inputKemonoDomain: String = "",
    val inputCoomerDomain: String = "",

    val updateInfo: AppUpdateInfo? = null,
)


sealed interface MainEffect {
    data class OpenUrl(val url: String) : MainEffect
}