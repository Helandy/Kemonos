package su.afk.kemonos.commonscreen.navigator

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.EntryPointAccessors
import su.afk.kemonos.commonscreen.ErrorScreen
import su.afk.kemonos.commonscreen.ErrorViewModel
import su.afk.kemonos.commonscreen.di.ErrorNavigatorEntryPoint
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import javax.inject.Inject

class ErrorNavigator @Inject constructor() : IErrorNavigator {
    override operator fun invoke(error: ErrorItem): NavKey = ErrorNavigatorDest(error = error)
}

class ErrorNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<ErrorNavigatorDest> { dest ->
            ErrorNavigatorEntry(dest)
        }
    }
}

@Composable
private fun ErrorNavigatorEntry(dest: ErrorNavigatorDest) {
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
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(dest) as T
            }
        }
    )

    ErrorScreen(viewModel = vm)
}