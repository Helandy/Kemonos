package su.afk.kemonos

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import su.afk.kemonos.error.error.ErrorMapper
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.LocalErrorMapper
import su.afk.kemonos.navigation.AppNavHost
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.presenter.bottomBar.BottomNavigationBar
import su.afk.kemonos.storage.api.videoPreview.IVideoFrameCache
import su.afk.kemonos.ui.imageLoader.LocalAppImageLoader
import su.afk.kemonos.ui.theme.KemonosTheme
import su.afk.kemonos.ui.video.LocalVideoFrameCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRoutingGraph @Inject constructor(
    private val domainResolver: IDomainResolver,
    private val imageLoader: ImageLoader,
    private val videoFrameCache: IVideoFrameCache,
    private val errorHandler: IErrorHandlerUseCase,
    private val navManager: NavigationManager,
    private val registrars: Set<@JvmSuppressWildcards NavRegistrar>
) {

    @Composable
    fun MainGraph() {
        KemonosTheme {
            val inTabs = navManager.startAppBackStack.isEmpty()
            Scaffold(
                modifier = Modifier,
                contentWindowInsets = WindowInsets(0),
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    if (inTabs) {
                        BottomNavigationBar(
                            currentTab = navManager.currentTab,
                            onTabClick = { tab -> navManager.switchTab(tab) }
                        )
                    }
                }
            ) { padding ->
                CompositionLocalProvider(
                    LocalDomainResolver provides domainResolver,
                    LocalAppImageLoader provides imageLoader,
                    LocalVideoFrameCache provides videoFrameCache,
                    LocalErrorMapper provides ErrorMapper { t -> errorHandler.parse(t) },
                ) {
                    AppNavHost(
                        navManager = navManager,
                        registrars = registrars,
                        modifier = Modifier.fillMaxSize().padding(padding),
                    )
                }
            }
        }
    }
}
