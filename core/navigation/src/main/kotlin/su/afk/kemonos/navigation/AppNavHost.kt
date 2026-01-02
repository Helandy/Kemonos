package su.afk.kemonos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import su.afk.kemonos.navigation.tab.BottomTab

@Composable
fun AppNavHost(
    navManager: NavigationManager,
    registrars: Set<@JvmSuppressWildcards NavRegistrar>,
    modifier: Modifier = Modifier
) {
    val provider = remember(registrars, navManager) {
        entryProvider<NavKey> {
            registrars.forEach { it.register(this, navManager) }
        }
    }

    /** entries для каждого таба создаём всегда (чтобы их держали decorators) */
    val creatorsEntries = key(BottomTab.CREATORS) {
        rememberDecoratedNavEntries(
            backStack = navManager.stack(BottomTab.CREATORS),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val postsEntries = key(BottomTab.POSTS) {
        rememberDecoratedNavEntries(
            backStack = navManager.stack(BottomTab.POSTS),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val profileEntries = key(BottomTab.PROFILE) {
        rememberDecoratedNavEntries(
            backStack = navManager.stack(BottomTab.PROFILE),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val appEntries = key("app") {
        rememberDecoratedNavEntries(
            backStack = navManager.startAppBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val entriesToShow: List<NavEntry<NavKey>> =
        if (navManager.startAppBackStack.isNotEmpty()) {
            appEntries
        } else {
            when (navManager.currentTab) {
                BottomTab.CREATORS -> creatorsEntries
                BottomTab.POSTS -> postsEntries
                BottomTab.PROFILE -> profileEntries
            }
        }

    /** используем overload NavDisplay, который принимает entries */
    NavDisplay(
        entries = entriesToShow,
        onBack = { navManager.back() },
        modifier = modifier
    )
}