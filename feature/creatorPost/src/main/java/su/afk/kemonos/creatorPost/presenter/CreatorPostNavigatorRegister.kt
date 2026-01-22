package su.afk.kemonos.creatorPost.presenter

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.EntryPointAccessors
import jakarta.inject.Inject
import su.afk.kemonos.common.presenter.baseScreen.ScreenNavigator
import su.afk.kemonos.creatorPost.di.CreatorPostVmFactoryEntryPoint
import su.afk.kemonos.creatorPost.navigation.CreatorPostDest
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager

class CreatorPostNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CreatorPostDest.CreatorPost> { dest ->
            CreatorPostEntry(dest)
        }
    }
}

@Composable
private fun CreatorPostEntry(dest: CreatorPostDest.CreatorPost) {
    val appContext = LocalContext.current.applicationContext

    val entryPoint = EntryPointAccessors.fromApplication(
        appContext,
        CreatorPostVmFactoryEntryPoint::class.java
    )
    val assistedFactory = entryPoint.creatorPostVmFactory()

    val viewModel: CreatorPostViewModel = viewModel(
        key = "CreatorPost:${dest}",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(dest) as T
            }
        }
    )

    ScreenNavigator(viewModel) { state, effect, event ->
        CreatorPostScreen(
            state = state,
            onEvent = event,
            effect = effect,
        )
    }
}