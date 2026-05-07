package su.afk.kemonos.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    var savedCurrentTab by rememberSaveable { mutableStateOf(navManager.currentTab) }
    LaunchedEffect(Unit) {
        navManager.restoreCurrentTab(savedCurrentTab)
    }
    LaunchedEffect(navManager.currentTab) {
        savedCurrentTab = navManager.currentTab
    }

    val startAppBackStack = rememberNavBackStack(navManager.mainDest)
    val creatorsBackStack = key(BottomTab.CREATORS) {
        rememberNavBackStack(navManager.roots.getValue(BottomTab.CREATORS))
    }
    val postsBackStack = key(BottomTab.POSTS) {
        rememberNavBackStack(navManager.roots.getValue(BottomTab.POSTS))
    }
    val profileBackStack = key(BottomTab.PROFILE) {
        rememberNavBackStack(navManager.roots.getValue(BottomTab.PROFILE))
    }
    val tabStacks = remember(creatorsBackStack, postsBackStack, profileBackStack) {
        mapOf(
            BottomTab.CREATORS to creatorsBackStack,
            BottomTab.POSTS to postsBackStack,
            BottomTab.PROFILE to profileBackStack,
        )
    }

    LaunchedEffect(startAppBackStack, tabStacks) {
        navManager.attachBackStacks(
            startAppBackStack = startAppBackStack,
            tabStacks = tabStacks,
        )
    }

    val provider = remember(registrars, navManager) {
        entryProvider<NavKey> {
            registrars.forEach { it.register(this, navManager) }
        }
    }

    /** entries для каждого таба создаём всегда (чтобы их держали decorators) */
    val creatorsEntries = key(BottomTab.CREATORS) {
        rememberDecoratedNavEntries(
            backStack = tabStacks.getValue(BottomTab.CREATORS),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val postsEntries = key(BottomTab.POSTS) {
        rememberDecoratedNavEntries(
            backStack = tabStacks.getValue(BottomTab.POSTS),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val profileEntries = key(BottomTab.PROFILE) {
        rememberDecoratedNavEntries(
            backStack = tabStacks.getValue(BottomTab.PROFILE),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val appEntries = key("app") {
        rememberDecoratedNavEntries(
            backStack = startAppBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = provider,
        )
    }

    val entriesToShow: List<NavEntry<NavKey>> =
        if (startAppBackStack.isNotEmpty()) {
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
