package su.afk.kemonos.creatorProfile.presenter.creatorProfile

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.EntryPointAccessors
import jakarta.inject.Inject
import su.afk.kemonos.creatorProfile.di.CreatorProfileVmFactoryEntryPoint
import su.afk.kemonos.creatorProfile.navigation.CreatorDest
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class CreatorProfileRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CreatorDest.CreatorProfile> { dest ->
            CreatorProfileEntry(dest)
        }
    }
}

@Composable
private fun CreatorProfileEntry(dest: CreatorDest.CreatorProfile) {
    val appContext = LocalContext.current.applicationContext

    val entryPoint = EntryPointAccessors.fromApplication(
        appContext,
        CreatorProfileVmFactoryEntryPoint::class.java
    )
    val assistedFactory = entryPoint.creatorProfileVmFactory()

    val viewModel: CreatorProfileViewModel = viewModel(
        key = "CreatorProfile:$dest",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(dest) as T
            }
        }
    )

    ScreenNavigator(viewModel) { state, effect, event ->
        CreatorScreen(
            state = state,
            onEvent = event,
            effect = effect,
        )
    }
}
