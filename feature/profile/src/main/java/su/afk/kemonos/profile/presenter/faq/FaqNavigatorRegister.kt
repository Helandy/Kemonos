package su.afk.kemonos.profile.presenter.faq

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDestination

class FaqNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDestination.Faq> {
            FaqScreen(onBack = nav::back)
        }
    }
}
