package su.afk.kemonos.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.domain.models.ErrorItem

interface IErrorNavigator {
    operator fun invoke(error: ErrorItem): NavKey
}