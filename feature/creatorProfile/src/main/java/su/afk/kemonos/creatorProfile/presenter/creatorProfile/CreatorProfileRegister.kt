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
import su.afk.kemonos.creatorProfile.navigation.CreatorDestination
import su.afk.kemonos.creatorProfile.presenter.discord.DiscordScreen
import su.afk.kemonos.creatorProfile.presenter.discord.DiscordViewModel
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class CreatorProfileRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<CreatorDestination.CreatorProfile> { dest ->
            CreatorProfileEntry(dest)
        }
    }
}

@Composable
private fun CreatorProfileEntry(dest: CreatorDestination.CreatorProfile) {
    val appContext = LocalContext.current.applicationContext

    val entryPoint = EntryPointAccessors.fromApplication(
        appContext,
        CreatorProfileVmFactoryEntryPoint::class.java
    )
    if (dest.service.equals("discord", ignoreCase = true)) {
        val assistedFactory = entryPoint.discordVmFactory()

        val viewModel: DiscordViewModel = viewModel(
            key = "CreatorDiscord:$dest",
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return assistedFactory.create(dest) as T
                }
            }
        )

        ScreenNavigator(viewModel) { state, effect, event ->
            DiscordScreen(
                state = state,
                onEvent = event,
                effect = effect,
            )
        }
    } else {
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
}
