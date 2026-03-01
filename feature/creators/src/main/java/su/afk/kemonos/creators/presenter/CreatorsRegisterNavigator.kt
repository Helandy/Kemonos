package su.afk.kemonos.creators.presenter

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.creators.navigation.CreatorsDestination
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareScreenNavigator

class CreatorsRegisterNavigator @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CreatorsDestination> {
            val viewModel = hiltViewModel<CreatorsViewModel>()

            SiteAwareScreenNavigator(viewModel) { state, effect, site, siteSwitching, event ->
                CreatorsScreen(
                    state = state,
                    onEvent = event,
                    effect = effect,
                    site = site,
                    siteSwitching = siteSwitching,
                )
            }
        }
    }
}