package su.afk.kemonos.setting.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

internal object SettingDestination {
    @Serializable
    data object Open : NavKey

    @Serializable
    data object Ui : NavKey

    @Serializable
    data object Video : NavKey

    @Serializable
    data object Translate : NavKey

    @Serializable
    data object TranslateModels : NavKey

    @Serializable
    data object Network : NavKey

    @Serializable
    data object Database : NavKey

    @Serializable
    data object Downloads : NavKey

    @Serializable
    data object HelpImport : NavKey

    @Serializable
    data object DebugStorage : NavKey

    @Serializable
    data object CreatorTabsOrder : NavKey
}
