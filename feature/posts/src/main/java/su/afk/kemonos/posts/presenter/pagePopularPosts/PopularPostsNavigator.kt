package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import su.afk.kemonos.common.presenter.changeSite.SiteAwareScreenNavigator

@Composable
internal fun PopularPostsNavigation() {
    val viewModel = hiltViewModel<PopularPostsViewModel>()
    SiteAwareScreenNavigator(viewModel) { state, effect, site, siteSwitching, event ->
        PopularPostsScreen(
            state = state,
            effect = effect,
            site = site,
            siteSwitching = siteSwitching,
            onEvent = event,
        )
    }
}
