package su.afk.kemonos.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/** Фичи реализуют этот интерфейс и регистрируют свои entry{} */
fun interface NavRegistrar {
    fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager)
}