package su.afk.kemonos

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import su.afk.kemonos.common.presenter.baseScreen.LocalAppScaffoldPadding
import su.afk.kemonos.navigation.AppNavHost
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.presenter.bottomBar.BottomNavigationBar
import su.afk.kemonos.ui.theme.KemonosTheme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRoutingGraph @Inject constructor(
    private val navManager: NavigationManager,
    private val registrars: Set<@JvmSuppressWildcards NavRegistrar>
) {

    @Composable
    fun MainGraph() {
        KemonosTheme {
            val background = MaterialTheme.colorScheme.background

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = background,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    val inTabs = navManager.appBackStack.isEmpty()
                    if (inTabs) {
                        BottomNavigationBar(
                            currentTab = navManager.currentTab,
                            onTabClick = { tab -> navManager.switchTab(tab) }
                        )
                    }
                }
            ) { innerPadding ->
                CompositionLocalProvider(LocalAppScaffoldPadding provides innerPadding) {
                    AppNavHost(
                        navManager = navManager,
                        registrars = registrars,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
