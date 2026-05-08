package su.afk.kemonos.commonscreen.imageViewScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import coil3.ImageLoader
import dagger.hilt.android.EntryPointAccessors
import su.afk.kemonos.commonscreen.di.CommonScreenVmFactoryEntryPoint
import su.afk.kemonos.commonscreen.navigator.CommonScreenDestination
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.di.ImageViewCoil
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator
import javax.inject.Inject

class ImageViewRegistrar @Inject constructor(
    @param:ImageViewCoil private val imageViewImageLoader: ImageLoader,
) : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CommonScreenDestination.ImageViewDest> { dest ->
            ImageViewEntry(dest, imageViewImageLoader)
        }
    }
}

@Composable
private fun ImageViewEntry(
    dest: CommonScreenDestination.ImageViewDest,
    imageViewImageLoader: ImageLoader,
) {
    val appContext = LocalContext.current.applicationContext
    val entryPoint = EntryPointAccessors.fromApplication(
        appContext,
        CommonScreenVmFactoryEntryPoint::class.java
    )
    val assistedFactory = entryPoint.imageViewVmFactory()

    val viewModel: ImageViewViewModel = viewModel(
        key = "ImageView:$dest",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return assistedFactory.create(dest, savedStateHandle) as T
            }
        }
    )

    ScreenNavigator(viewModel) { state, effect, event ->
        ImageViewScreen(
            state = state,
            effect = effect,
            onEvent = event,
            imageLoader = imageViewImageLoader,
        )
    }
}
