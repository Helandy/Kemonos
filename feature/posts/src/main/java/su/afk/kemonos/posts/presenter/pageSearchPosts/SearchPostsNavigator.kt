package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import su.afk.kemonos.common.presenter.changeSite.SiteAwareScreenNavigator

@Composable
internal fun SearchPostsNavigation() {
    val viewModel = hiltViewModel<SearchPostsViewModel>()
    SiteAwareScreenNavigator(viewModel) { state, effect, site, siteSwitching, event ->
        SearchPostsScreen(
            state = state,
            effect = effect,
            site = site,
            siteSwitching = siteSwitching,
            onEvent = event,
        )
    }
}
