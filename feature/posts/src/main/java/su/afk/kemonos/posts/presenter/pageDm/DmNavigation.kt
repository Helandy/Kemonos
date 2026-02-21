package su.afk.kemonos.posts.presenter.pageDm

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareScreenNavigator

@Composable
internal fun DmNavigation() {
    val viewModel = hiltViewModel<DmViewModel>()
    SiteAwareScreenNavigator(viewModel) { state, effect, site, siteSwitching, event ->
        DmScreen(
            state = state,
            effect = effect,
            site = site,
            siteSwitching = siteSwitching,
            onEvent = event,
        )
    }
}
