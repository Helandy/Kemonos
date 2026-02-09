package su.afk.kemonos.posts.presenter.pageTags

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import su.afk.kemonos.common.presenter.changeSite.SiteAwareScreenNavigator

@Composable
internal fun TagsPageNavigation() {
    val viewModel = hiltViewModel<TagsPageViewModel>()
    SiteAwareScreenNavigator(viewModel) { state, effect, site, siteSwitching, event ->
        TagsPageScreen(
            state = state,
            effect = effect,
            site = site,
            siteSwitching = siteSwitching,
            onEvent = event,
        )
    }
}
