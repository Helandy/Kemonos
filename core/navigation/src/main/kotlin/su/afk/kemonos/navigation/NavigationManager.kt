package su.afk.kemonos.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.navigation.tab.BottomTab

class NavigationManager(
    private val roots: Map<BottomTab, NavKey>,
    private val mainDest: NavKey,
    initialTab: BottomTab,
) {

    /** Стек MainDest при запуске */
    val startAppBackStack: SnapshotStateList<NavKey> = mutableStateListOf(mainDest)

    /** Активный стек либо startAppBackStack, либо стек табов */
    val backStack: SnapshotStateList<NavKey>
        get() = if (startAppBackStack.isNotEmpty()) startAppBackStack else stacks.getValue(currentTab)

    private val stacks: Map<BottomTab, SnapshotStateList<NavKey>> =
        BottomTab.entries.associateWith { mutableStateListOf<NavKey>() }

    /** Стек табов при работе */
    var currentTab: BottomTab by mutableStateOf(initialTab)
        private set

    fun stack(tab: BottomTab): SnapshotStateList<NavKey> = stacks.getValue(tab)

    init {
        BottomTab.entries.forEach { tab ->
            val stack = stacks.getValue(tab)
            if (stack.isEmpty()) stack += roots.getValue(tab)
        }
    }

    /** уходим с MainDest в табы */
    fun enterTabs() {
        startAppBackStack.clear()
    }

    fun switchTab(tab: BottomTab, reselectPopToRoot: Boolean = true) {
        if (tab == currentTab) {
            if (reselectPopToRoot) popToRoot()
            return
        }
        currentTab = tab
    }

    fun navigate(dest: NavKey) {
        backStack += dest
    }

    fun replace(dest: NavKey) {
        if (backStack.isNotEmpty()) backStack[backStack.lastIndex] = dest
        else backStack += dest
    }

    fun back() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun backTwo() {
        repeat(2) {
            if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            else {
                if (backStack.isEmpty()) backStack += roots.getValue(currentTab)
                return
            }
        }
        if (backStack.isEmpty()) backStack += roots.getValue(currentTab)
    }

    fun popBackTo(dest: NavKey, inclusive: Boolean = false) {
        val index = backStack.indexOf(dest)
        if (index == -1) return

        val removeFrom = if (inclusive) index else index + 1
        for (i in backStack.lastIndex downTo removeFrom) {
            backStack.removeAt(i)
        }
        if (backStack.isEmpty()) backStack += roots.getValue(currentTab)
    }

    fun popToRoot() {
        val root = roots.getValue(currentTab)
        backStack.clear()
        backStack += root
    }

    fun resetAllTabs() {
        BottomTab.entries.forEach { tab ->
            stacks.getValue(tab).apply {
                clear()
                add(roots.getValue(tab))
            }
        }
        currentTab = BottomTab.CREATORS
    }
}