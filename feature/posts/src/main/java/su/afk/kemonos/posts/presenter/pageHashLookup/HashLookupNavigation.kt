package su.afk.kemonos.posts.presenter.pageHashLookup

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareScreenNavigator

@Composable
internal fun HashLookupNavigation() {
    val viewModel = hiltViewModel<HashLookupViewModel>()
    SiteAwareScreenNavigator(viewModel) { state, effect, site, siteSwitching, event ->
        HashLookupScreen(
            state = state,
            effect = effect,
            site = site,
            siteSwitching = siteSwitching,
            onEvent = event,
        )
    }
}
