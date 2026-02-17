package su.afk.kemonos.commonscreen.errorScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.EntryPointAccessors
import su.afk.kemonos.commonscreen.di.ErrorNavigatorEntryPoint
import su.afk.kemonos.commonscreen.navigator.CommonScreenDest
import su.afk.kemonos.commonscreen.navigator.IErrorNavigator
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator
import javax.inject.Inject

class ErrorNavigator @Inject constructor() : IErrorNavigator {
    override operator fun invoke(error: ErrorItem): NavKey = CommonScreenDest.ErrorNavigatorDest(error = error)
}

class ErrorNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CommonScreenDest.ErrorNavigatorDest> { dest ->
            ErrorNavigatorEntry(dest)
        }
    }
}

@Composable
private fun ErrorNavigatorEntry(dest: CommonScreenDest.ErrorNavigatorDest) {
    val appContext = LocalContext.current.applicationContext

    val entryPoint = EntryPointAccessors.fromApplication(
        appContext,
        ErrorNavigatorEntryPoint::class.java
    )
    val assistedFactory = entryPoint.creatorErrorViewModelFactory()

    val vm: ErrorViewModel = viewModel(
        key = "ErrorNavigatorDest:${dest}",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(dest) as T
            }
        }
    )

    ScreenNavigator(vm) { state, effect, event ->
        ErrorScreen(
            state = state,
            onEvent = event,
            effect = effect,
        )
    }
}
