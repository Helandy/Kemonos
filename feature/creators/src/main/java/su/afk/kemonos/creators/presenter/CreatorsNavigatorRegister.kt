package su.afk.kemonos.creators.presenter

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.common.presenter.changeSite.SiteAwareScreenNavigator
import su.afk.kemonos.creators.navigation.CreatorsDest
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager

class CreatorsNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CreatorsDest> {
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