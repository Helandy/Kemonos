package su.afk.kemonos.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.navigation.tab.BottomTab

class NavigationManager(
    val roots: Map<BottomTab, NavKey>,
    val mainDest: NavKey,
    initialTab: BottomTab,
) {

    /** Стек MainDest при запуске */
    var startAppBackStack: MutableList<NavKey> by mutableStateOf(mutableStateListOf(mainDest))
        private set

    /** Активный стек либо startAppBackStack, либо стек табов */
    val backStack: MutableList<NavKey>
        get() = if (startAppBackStack.isNotEmpty()) startAppBackStack else stacks.getValue(currentTab)

    private var stacks: Map<BottomTab, MutableList<NavKey>> by mutableStateOf(
        BottomTab.entries.associateWith { mutableStateListOf<NavKey>() }
    )

    /** Стек табов при работе */
    var currentTab: BottomTab by mutableStateOf(initialTab)
        private set

    fun stack(tab: BottomTab): MutableList<NavKey> = stacks.getValue(tab)

    init {
        BottomTab.entries.forEach { tab ->
            val stack = stacks.getValue(tab)
            if (stack.isEmpty()) stack += roots.getValue(tab)
        }
    }

    fun attachBackStacks(
        startAppBackStack: NavBackStack<NavKey>,
        tabStacks: Map<BottomTab, NavBackStack<NavKey>>,
    ) {
        if (this.startAppBackStack.hasPendingNavigation(mainDest) &&
            startAppBackStack.isInitialStack(mainDest)
        ) {
            startAppBackStack.replaceWith(this.startAppBackStack)
        }

        BottomTab.entries.forEach { tab ->
            val root = roots.getValue(tab)
            val currentStack = stacks.getValue(tab)
            val saveableStack = tabStacks.getValue(tab)
            if (currentStack.hasPendingNavigation(root) && saveableStack.isInitialStack(root)) {
                saveableStack.replaceWith(currentStack)
            }
        }

        this.startAppBackStack = startAppBackStack
        this.stacks = tabStacks
    }

    fun restoreCurrentTab(tab: BottomTab) {
        currentTab = tab
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

    private fun List<NavKey>.isInitialStack(root: NavKey): Boolean =
        size == 1 && firstOrNull() == root

    private fun List<NavKey>.hasPendingNavigation(root: NavKey): Boolean =
        !isInitialStack(root)

    private fun MutableList<NavKey>.replaceWith(items: List<NavKey>) {
        clear()
        addAll(items)
    }
}
