package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import coil3.ImageLoader
import su.afk.kemonos.commonscreen.navigator.CommonScreenDest
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.di.ImageViewCoil
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator
import javax.inject.Inject

class ImageViewRegistrar @Inject constructor(
    @param:ImageViewCoil private val imageViewImageLoader: ImageLoader,
) : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CommonScreenDest.ImageViewDest> {
            val viewModel = hiltViewModel<ImageViewViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                ImageViewScreen(
                    state = state,
                    effect = effect,
                    onEvent = event,
                    imageLoader = imageViewImageLoader,
                )
            }
        }
    }
}
