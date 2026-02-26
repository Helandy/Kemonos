package su.afk.kemonos.creatorProfile.presenter.communityChat

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

class CommunityChatRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CreatorDest.CommunityChat> { dest ->
            CommunityChatEntry(dest)
        }
    }
}

@Composable
private fun CommunityChatEntry(dest: CreatorDest.CommunityChat) {
    val appContext = LocalContext.current.applicationContext

    val entryPoint = EntryPointAccessors.fromApplication(
        appContext,
        CreatorProfileVmFactoryEntryPoint::class.java
    )
    val assistedFactory = entryPoint.communityChatVmFactory()

    val viewModel: CommunityChatViewModel = viewModel(
        key = "CreatorCommunityChat:$dest",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(dest) as T
            }
        }
    )

    ScreenNavigator(viewModel) { state, effect, event ->
        CommunityChatScreen(
            state = state,
            onEvent = event,
            effect = effect,
        )
    }
}
